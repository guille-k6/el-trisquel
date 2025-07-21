package trisquel.afip.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trisquel.afip.AfipSoapRequestBuilder;
import trisquel.afip.AfipUtils;
import trisquel.afip.auth.AfipAuthService;
import trisquel.afip.config.AfipConfiguration;
import trisquel.afip.model.AfipAuth;
import trisquel.afip.model.AfipInvoiceRequest;
import trisquel.afip.model.AfipIva;
import trisquel.model.*;
import trisquel.repository.InvoiceQueueRepository;
import trisquel.service.InvoiceService;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class InvoiceProcessingService {

    private final InvoiceQueueRepository invoiceQueueRepository;
    private final AfipInvoiceService afipInvoiceService;
    private final AfipConfiguration afipConfig;
    private final InvoiceService invoiceService;
    private final AfipAuthService authService;
    private final AfipAuthService afipAuthService;

    private final String trisquelCUIT = "30717409775";

    public InvoiceProcessingService(InvoiceQueueRepository invoiceQueueRepository,
                                    AfipInvoiceService afipInvoiceService, AfipConfiguration afipConfig,
                                    InvoiceService invoiceService, AfipAuthService authService,
                                    AfipAuthService afipAuthService) {
        this.invoiceQueueRepository = invoiceQueueRepository;
        this.afipInvoiceService = afipInvoiceService;
        this.afipConfig = afipConfig;
        this.invoiceService = invoiceService;
        this.authService = authService;
        this.afipAuthService = afipAuthService;
    }

    public void processQueuedInvoices() {
        List<InvoiceQueueStatus> statusesToProcess = Arrays.asList(InvoiceQueueStatus.QUEUED, InvoiceQueueStatus.FAILING);
        List<InvoiceQueue> invoicesToProcess = invoiceQueueRepository.findByStatusInOrderByEnqueuedAtAsc(statusesToProcess);
        // TODO: Handle retries logic? How many?
        for (InvoiceQueue invoiceQueue : invoicesToProcess) {
            try {
                processInvoice(invoiceQueue);
            } catch (Exception e) {
                // handleProcessingError(invoiceQueue, e.getMessage());
            }
        }
    }

    private void processInvoice(InvoiceQueue invoiceQueue) {
        try {
            updateInvoiceStatus(invoiceQueue);
            AfipAuth afipAuth = afipAuthService.authenticate();
            Optional<Invoice> optInvoice = invoiceService.findById(invoiceQueue.getInvoiceId());
            if (optInvoice.isEmpty()) {
                throw new RuntimeException("Invoice not found: " + invoiceQueue.getInvoiceId());
            }
            Invoice invoice = optInvoice.get();
            Client client = invoice.getClient();
            InvoiceIvaBreakdown invoiceBreakdown = new InvoiceIvaBreakdown(invoice);
            // AfipInvoiceRequest afipRequest = buildAfipRequest(invoiceQueue, afipAuth);
            String request = AfipSoapRequestBuilder.buildFECAESolicitarRequest(afipAuth, trisquelCUIT, invoice, client, invoiceBreakdown);
            invoiceQueue.setRequest(request);
            // Procesar la factura
            // handleAfipResponse(invoiceQueue, afipResponse);
        } catch (Exception e) {
            // handleProcessingError(invoiceQueue, e.getMessage());
        }
    }

    private AfipInvoiceRequest buildAfipRequest(InvoiceQueue invoiceQueue, AfipAuth afipAuth) {
        AfipInvoiceRequest afipRequest = new AfipInvoiceRequest();
        Optional<Invoice> optInvoice = invoiceService.findById(invoiceQueue.getInvoiceId());
        if (optInvoice.isEmpty()) {
            throw new RuntimeException("Invoice not found: " + invoiceQueue.getInvoiceId());
        }
        Invoice invoice = optInvoice.get();
        Client client = invoice.getClient();
        InvoiceIvaBreakdown invoiceBreakdown = new InvoiceIvaBreakdown(invoice);
        //@formatter:off
        String request = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                        "xmlns:ar=\"http://ar.gov.afip.dif.FEV1/\">\n" +
                        "    <soapenv:Header/>\n" +
                        "    <soapenv:Body>\n" +
                        "        <ar:FECAESolicitar>\n" +
                        "            <!--Optional:-->\n" +
                        "            <ar:Auth>\n" +
                        "                <ar:Token>" + afipAuth.getToken() + "</ar:Token>\n" +
                        "                <ar:Sign>" + afipAuth.getSign() + "</ar:Sign>\n" +
                        "                <ar:Cuit>" + trisquelCUIT + "</ar:Cuit>\n" +
                        "            </ar:Auth>\n" +
                        "            <ar:FeCAEReq>\n" +
                        "                <ar:FeCabReq>\n" +
                        "                    <ar:CantReg>" + 1 + "</ar:CantReg>\n" +
                        "                    <ar:PtoVta>" + invoice.getSellPoint() + "</ar:PtoVta>\n" +
                        "                    <ar:CbteTipo>" + invoice.getComprobante().getCode() + "</ar:CbteTipo>\n" +
                        "                </ar:FeCabReq>\n" +
                        "                <ar:FeDetReq>\n" +
                        "                    <ar:FECAEDetRequest>\n" +
                        "                        <ar:Concepto>" + invoice.getConcepto().getCode() + "</ar:Concepto>\n" +
                        "                        <ar:DocTipo>" + client.getDocType().getCode() + "</ar:DocTipo>\n" +
                        "                        <ar:DocNro>" + client.getDocNumber().toString() + "</ar:DocNro>\n" +
                        "                        <ar:CbteDesde>" + 1 + "</ar:CbteDesde>\n" +
                        "                        <ar:CbteHasta>" + 1 + "</ar:CbteHasta>\n" +
                        "                        <ar:CbteFch>" + AfipUtils.toAfipDateFormat(invoice.getDate()) + "</ar:CbteFch>\n" +
                        "                        <ar:ImpTotal>" + AfipUtils.toAfipNumberFormat(invoiceBreakdown.getInvoiceTotal()) + "</ar:ImpTotal>\n" +
                        "                        <ar:ImpTotConc>" + 0 + "</ar:ImpTotConc>\n" +
                        "                        <ar:ImpNeto>" + AfipUtils.toAfipNumberFormat(invoiceBreakdown.getInvoiceNetTotal()) + "</ar:ImpNeto>\n" +
                        "                        <ar:ImpOpEx>" + 0 + "</ar:ImpOpEx>\n" +
                        "                        <ar:ImpTrib>" + 0 + "</ar:ImpTrib>\n" +
                        "                        <ar:ImpIVA>" + AfipUtils.toAfipNumberFormat(invoiceBreakdown.getIvaTotal()) + "</ar:ImpIVA>\n" +
                        "                        <ar:FchServDesde></ar:FchServDesde>\n" +
                        "                        <ar:FchServHasta></ar:FchServHasta>\n" +
                        "                        <ar:FchVtoPago></ar:FchVtoPago>\n" +
                        "                        <ar:MonId>" + invoice.getMoneda().getCode() + "</ar:MonId>\n" +
                        "                        <ar:MonCotiz>" + 1 + "</ar:MonCotiz>\n" +
                        "                        <ar:CondicionIVAReceptorId>" + client.getCondicionIva().getCode() + "</ar:CondicionIVAReceptorId>\n" + /*
                        "                        <ar:Tributos>\n" +
                        "                            <ar:Tributo>\n" +
                        "                                <ar:Id>99</ar:Id>\n" +
                        "                                <ar:Desc>Impuesto Municipal Matanza</ar:Desc>\n" +
                        "                                <ar:BaseImp>150</ar:BaseImp>\n" +
                        "                                <ar:Alic>5.2</ar:Alic>\n" +
                        "                                <ar:Importe>7.8</ar:Importe>\n" +
                        "                            </ar:Tributo>\n" +
                        "                        </ar:Tributos>\n" +
                                                                                                                                                         */
                                                buildAfipIvaXml(invoiceBreakdown) +
                        "                    </ar:FECAEDetRequest>\n" +
                        "                </ar:FeDetReq>\n" +
                        "            </ar:FeCAEReq>\n" +
                        "        </ar:FECAESolicitar>\n" +
                        "    </soapenv:Body>\n" +
                        "</soapenv:Envelope>";
        //@formatter:on

        invoiceQueue.setRequest(request);
        return afipRequest;
    }

    public String buildAfipIvaXml(InvoiceIvaBreakdown invoiceBreakdown) {
        StringBuilder xml = new StringBuilder();
        xml.append("<ar:Iva>\n");
        for (Map.Entry<AfipIva, InvoiceIvaBreakdown.IvaData> entry : invoiceBreakdown.getIvaMap().entrySet()) {
            AfipIva tipoIva = entry.getKey();
            InvoiceIvaBreakdown.IvaData ivaData = entry.getValue();

            xml.append("<ar:AlicIva>\n");
            xml.append("<ar:Id>").append(tipoIva.getCode()).append("</ar:Id>\n");
            xml.append("<ar:BaseImp>").append(AfipUtils.toAfipNumberFormat(ivaData.getBaseImponible())).append("</ar:BaseImp>\n");
            xml.append("<ar:Importe>").append(AfipUtils.toAfipNumberFormat(ivaData.getImporteIva())).append("</ar:Importe>\n");
            xml.append("</ar:AlicIva>\n");
        }
        xml.append("</ar:Iva>\n");
        return xml.toString();
    }

    private void updateInvoiceStatus(InvoiceQueue invoiceQueue) {
        if (invoiceQueue.getStatus() == InvoiceQueueStatus.QUEUED) {
            invoiceQueue.setStatus(InvoiceQueueStatus.STARTED);
        } else if (invoiceQueue.getStatus() == InvoiceQueueStatus.FAILING) {
            invoiceQueue.setStatus(InvoiceQueueStatus.RETRYING);
            invoiceQueue.setRetryCount(invoiceQueue.getRetryCount() + 1);
        }
        invoiceQueue.setProcessedAt(ZonedDateTime.now());
        invoiceService.handleInvoiceQueueSave(invoiceQueue);
    }
}
