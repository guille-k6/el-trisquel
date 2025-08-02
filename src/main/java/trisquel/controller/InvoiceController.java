package trisquel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trisquel.model.Dto.InvoiceDTO;
import trisquel.model.Dto.InvoiceInputDTO;
import trisquel.model.Invoice;
import trisquel.model.InvoiceQueueStatus;
import trisquel.service.InvoiceService;
import trisquel.utils.ValidationException;
import trisquel.utils.ValidationExceptionResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public ResponseEntity<Page<InvoiceDTO>> getAllInvoices(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                           @RequestParam(required = false) Long clientId,
                                                           @RequestParam(required = false) InvoiceQueueStatus status) {

        Page<InvoiceDTO> invoices = invoiceService.findAll(page, startDate, endDate, clientId, status);
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable Long id) {
        return invoiceService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ValidationExceptionResponse(Map.of("Error", List.of(e.getMessage()))));
        }
        return response;
    }
}
