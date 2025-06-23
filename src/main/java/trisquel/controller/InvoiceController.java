package trisquel.controller;

import trisquel.model.Dto.InvoiceInputDTO;
import trisquel.model.Invoice;
import trisquel.model.InvoiceQueue;
import trisquel.service.InvoiceService;
import trisquel.utils.ValidationException;
import trisquel.utils.ValidationExceptionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public List<Invoice> getAllInvoices() {
        return invoiceService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable Long id) {
        return invoiceService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Invoice> createInvoice(@RequestBody Invoice invoice) {
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceService.save(invoice));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Invoice> updateInvoice(@RequestBody Invoice invoice) {
        return ResponseEntity.ok(invoiceService.save(invoice));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        invoiceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/new")
    public ResponseEntity<?> newInvoice(@RequestBody InvoiceInputDTO invoiceInputDTO) {
        ResponseEntity<?> response;
        try {
            invoiceService.processNewInvoiceRequest(invoiceInputDTO);
            response = ResponseEntity.ok("");
        } catch (ValidationException e) {
            response = ResponseEntity.status(HttpStatus.CONFLICT).body(new ValidationExceptionResponse(e.getValidationErrors()).getErrors());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return response;
    }

    @GetMapping("/test")
    public ResponseEntity<Void> testQueue() {
        InvoiceQueue invQueue = new InvoiceQueue(1L);
        invQueue.setCae("holaSoyUnCae");
        invoiceService.enqueueInvoice(invQueue);
        return ResponseEntity.noContent().build();
    }
}
