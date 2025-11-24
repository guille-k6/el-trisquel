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
import trisquel.model.InvoiceQueueStatus;
import trisquel.service.ConfigurationService;
import trisquel.service.InvoiceReportService;
import trisquel.service.InvoiceService;
import trisquel.service.PrinterService;

import java.awt.image.BufferedImage;
import java.util.Optional;

@RestController
@RequestMapping("/invoice_printer")
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
        BufferedImage qr = null; // o generateQrImage(...)

        Optional<Invoice> invoice = invoiceService.findInvoiceById(id);
        if (invoice.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (invoice.get().getStatus() != InvoiceQueueStatus.COMPLETED) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        InvoiceIvaBreakdown ivaBreakdown = new InvoiceIvaBreakdown(invoice.get());
        Optional<ConfigurationMap> orgInfo = configurationService.findByKey("org");

        byte[] otroPdf = invoiceReportService.generateInvoiceReport(invoice.get(), ivaBreakdown, orgInfo.get());

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=FacturaElectronicaAfip_" + id + ".pdf").header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, max-age=0").contentType(MediaType.APPLICATION_PDF).body(otroPdf);
    }

}
