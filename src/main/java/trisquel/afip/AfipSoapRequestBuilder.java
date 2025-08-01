package trisquel.afip;

import trisquel.afip.model.AfipAuth;
import trisquel.afip.model.AfipIva;
import trisquel.model.Client;
import trisquel.model.Invoice;
import trisquel.model.InvoiceIvaBreakdown;

import java.util.Map;

public class AfipSoapRequestBuilder {
    //@formatter:off
    public static String buildFECAESolicitarRequest(AfipAuth afipAuth, String trisquelCUIT,
                                             Invoice invoice, Client client,
                                             InvoiceIvaBreakdown invoiceBreakdown) {
        final String CUIT = "30717409775"; // TODO: Evitar hardcode y poner el del trisquel.


        StringBuilder xml = new StringBuilder();

        // Header SOAP
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                .append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" ")
                .append("xmlns:ar=\"http://ar.gov.afip.dif.FEV1/\">")
                .append("<soapenv:Header/>")
                .append("<soapenv:Body>");

        // FECAESolicitar
        xml.append("<ar:FECAESolicitar>");
        appendAuth(xml, afipAuth, CUIT);
        appendFeCAEReq(xml, invoice, client, invoiceBreakdown);
        xml.append("</ar:FECAESolicitar>");

        // Footer SOAP
        xml.append("</soapenv:Body></soapenv:Envelope>");

        return xml.toString();
    }

    private static void appendAuth(StringBuilder xml, AfipAuth afipAuth, String trisquelCUIT) {
        xml.append("<ar:Auth>")
            .append("<ar:Token>").append(afipAuth.getToken()).append("</ar:Token>")
            .append("<ar:Sign>").append(afipAuth.getSign()).append("</ar:Sign>")
            .append("<ar:Cuit>").append(trisquelCUIT).append("</ar:Cuit>")
        .append("</ar:Auth>");
    }

    private static void appendFeCAEReq(StringBuilder xml, Invoice invoice, Client client,
                                InvoiceIvaBreakdown invoiceBreakdown) {
        xml.append("<ar:FeCAEReq>");

        // Cabecera
        xml.append("<ar:FeCabReq>")
                .append("<ar:CantReg>1</ar:CantReg>")
                .append("<ar:PtoVta>").append(invoice.getSellPoint()).append("</ar:PtoVta>")
                .append("<ar:CbteTipo>").append(invoice.getComprobante().getCode()).append("</ar:CbteTipo>")
                .append("</ar:FeCabReq>");

        // Detalle
        xml.append("<ar:FeDetReq>")
                .append("<ar:FECAEDetRequest>");

        appendDetRequest(xml, invoice, client, invoiceBreakdown);

        xml.append("</ar:FECAEDetRequest>")
                .append("</ar:FeDetReq>")
                .append("</ar:FeCAEReq>");
    }

    private static void appendDetRequest(StringBuilder xml, Invoice invoice, Client client,
                                  InvoiceIvaBreakdown invoiceBreakdown) {
        xml.append("<ar:Concepto>").append(invoice.getConcepto().getCode()).append("</ar:Concepto>")
                .append("<ar:DocTipo>").append(client.getDocType().getCode()).append("</ar:DocTipo>")
                .append("<ar:DocNro>").append(client.getDocNumber()).append("</ar:DocNro>")
                .append("<ar:CbteDesde>1</ar:CbteDesde>")
                .append("<ar:CbteHasta>1</ar:CbteHasta>")
                .append("<ar:CbteFch>").append(AfipUtils.toAfipDateFormat(invoice.getDate())).append("</ar:CbteFch>")
                .append("<ar:ImpTotal>").append(AfipUtils.toAfipNumberFormat(invoiceBreakdown.getInvoiceTotal())).append("</ar:ImpTotal>")
                .append("<ar:ImpTotConc>0</ar:ImpTotConc>")
                .append("<ar:ImpNeto>").append(AfipUtils.toAfipNumberFormat(invoiceBreakdown.getInvoiceNetTotal())).append("</ar:ImpNeto>")
                .append("<ar:ImpOpEx>0</ar:ImpOpEx>")
                .append("<ar:ImpTrib>0</ar:ImpTrib>")
                .append("<ar:ImpIVA>").append(AfipUtils.toAfipNumberFormat(invoiceBreakdown.getIvaTotal())).append("</ar:ImpIVA>")
                .append("<ar:FchServDesde></ar:FchServDesde>")
                .append("<ar:FchServHasta></ar:FchServHasta>")
                .append("<ar:FchVtoPago></ar:FchVtoPago>")
                .append("<ar:MonId>").append(invoice.getMoneda().getCode()).append("</ar:MonId>")
                .append("<ar:MonCotiz>1</ar:MonCotiz>")
                .append("<ar:CondicionIVAReceptorId>").append(client.getCondicionIva().getCode()).append("</ar:CondicionIVAReceptorId>");
        // Agregar IVA (sin indentación, XML compacto)
        xml.append(buildCompactAfipIvaXml(invoiceBreakdown));
    }

    private static String buildCompactAfipIvaXml(InvoiceIvaBreakdown invoiceBreakdown) {
        StringBuilder xml = new StringBuilder();
        xml.append("<ar:Iva>");
        for (Map.Entry<AfipIva, InvoiceIvaBreakdown.IvaData> entry : invoiceBreakdown.getIvaMap().entrySet()) {
            AfipIva tipoIva = entry.getKey();
            InvoiceIvaBreakdown.IvaData ivaData = entry.getValue();
            xml.append("<ar:AlicIva>")
                    .append("<ar:Id>").append(tipoIva.getCode()).append("</ar:Id>")
                    .append("<ar:BaseImp>").append(AfipUtils.toAfipNumberFormat(ivaData.getBaseImponible())).append("</ar:BaseImp>")
                    .append("<ar:Importe>").append(AfipUtils.toAfipNumberFormat(ivaData.getImporteIva())).append("</ar:Importe>")
               .append("</ar:AlicIva>");
        }

        xml.append("</ar:Iva>");
        return xml.toString();
    }
    //@formatter:on
}
