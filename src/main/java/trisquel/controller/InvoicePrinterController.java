package trisquel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import trisquel.model.ConfigurationMap;
import trisquel.model.Invoice;
import trisquel.model.InvoiceIvaBreakdown;
import trisquel.service.ConfigurationService;
import trisquel.service.InvoiceReportService;
import trisquel.service.InvoiceService;
import trisquel.service.PrinterService;

import java.awt.image.BufferedImage;
import java.util.Optional;

@RestController
@RequestMapping("/invoice-printer")
public class InvoicePrinterController {

    private final InvoiceService invoiceService;
    private final PrinterService printerService;
    private final InvoiceReportService invoiceReportService;
    private final ConfigurationService configurationService;

    @Autowired
    public InvoicePrinterController(PrinterService printerService, InvoiceReportService invoiceReportService,
                                    InvoiceService invoiceService, ConfigurationService configurationService) {
        this.printerService = printerService;
        this.invoiceReportService = invoiceReportService;
        this.invoiceService = invoiceService;
        this.configurationService = configurationService;

    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getInvoicePdf(@PathVariable Long id) throws Exception {
        // 1) Traés tu factura e items desde DB/servicio y armás params + items:
        BufferedImage qr = null; // o generateQrImage(...)

        Optional<Invoice> invoice = invoiceService.findInvoiceById(id);
        if (invoice.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        InvoiceIvaBreakdown ivaBreakdown = new InvoiceIvaBreakdown(invoice.get());
        Optional<ConfigurationMap> orgInfo = configurationService.findByKey("org");

        byte[] otroPdf = invoiceReportService.generateInvoiceReport(invoice.get(), ivaBreakdown, orgInfo.get());

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=FacturaElectronicaAfip_" + id + ".pdf").header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, max-age=0").contentType(MediaType.APPLICATION_PDF).body(otroPdf);
        //        Map<String, ?> params = printerService.invoiceParams(invoice.get(), ivaBreakdown, orgInfo.get());
        //        List<Map<String, ?>> items = printerService.exampleItems();

        // IMPORTANTe: asegurate que los nombres de keys coincidan con los field del JRXML
        //        byte[] pdf = printerService.generateInvoicePdf(params, items);
        //        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=factura-" + id + ".pdf").contentType(MediaType.APPLICATION_PDF).contentLength(pdf.length).body(pdf);
    }

    @GetMapping(value = "/invoice-demo", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> invoiceDemo() throws Exception {

        byte[] pdf = invoiceReportService.generateDemoPdf();
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=FacturaElectronica_AFIP_demo.pdf").header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, max-age=0").contentType(MediaType.APPLICATION_PDF).body(pdf);
    }

}
