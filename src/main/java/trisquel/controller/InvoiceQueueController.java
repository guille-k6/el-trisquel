package trisquel.controller;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trisquel.model.InvoiceQueue;
import trisquel.model.InvoiceQueueStatus;
import trisquel.service.InvoiceQueueService;
import trisquel.utils.ValidationException;
import trisquel.utils.ValidationExceptionResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/invoice-queue")
public class InvoiceQueueController {

    // private final InvoiceScheduler invoiceScheduler;
    private final InvoiceQueueService invoiceQueueService;

    public InvoiceQueueController(InvoiceQueueService invoiceQueueService) {
        // this.invoiceScheduler = invoiceScheduler;
        this.invoiceQueueService = invoiceQueueService;
    }

    @GetMapping
    public ResponseEntity<?> getAllInvoiceQueues(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
                                                 @RequestParam(required = false) InvoiceQueueStatus queueStatus) {
        ResponseEntity<?> response;
        try {
            Page<InvoiceQueue> invoiceQueues = invoiceQueueService.findAll(page, dateFrom, dateTo, queueStatus);
            return ResponseEntity.ok(invoiceQueues);
        } catch (ValidationException e) {
            response = ResponseEntity.status(HttpStatus.CONFLICT).body(new ValidationExceptionResponse(e.getValidationErrors()).getErrors());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ValidationExceptionResponse(Map.of("Error", List.of(e.getMessage()))));
        }
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInvoiceQueue(@PathVariable Long id) {
        ResponseEntity<?> response;
        try {
            Optional<InvoiceQueue> invoiceQueues = invoiceQueueService.findById(id);
            if (invoiceQueues.isPresent()) {
                response = ResponseEntity.ok(invoiceQueues.get());
            } else {
                response = ResponseEntity.notFound().build();
            }
        } catch (ValidationException e) {
            response = ResponseEntity.status(HttpStatus.CONFLICT).body(new ValidationExceptionResponse(e.getValidationErrors()).getErrors());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ValidationExceptionResponse(Map.of("Error", List.of(e.getMessage()))));
        }
        return response;
    }

    @GetMapping("/invoice/{id}")
    public ResponseEntity<?> getInvoicesQueuesForInvoice(@PathVariable Long id) {
        ResponseEntity<?> response;
        try {
            List<InvoiceQueue> invoiceQueues = invoiceQueueService.findByInvoice(id);
            return ResponseEntity.ok(invoiceQueues);
        } catch (ValidationException e) {
            response = ResponseEntity.status(HttpStatus.CONFLICT).body(new ValidationExceptionResponse(e.getValidationErrors()).getErrors());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ValidationExceptionResponse(Map.of("Error", List.of(e.getMessage()))));
        }
        return response;
    }

    @GetMapping("/retry/{id}")
    public ResponseEntity<?> retryInvoiceQueue(@PathVariable Long id) {
        ResponseEntity<?> response;
        try {
            invoiceQueueService.retryInvoiceQueue(id);
        } catch (ValidationException e) {
            response = ResponseEntity.status(HttpStatus.CONFLICT).body(new ValidationExceptionResponse(e.getValidationErrors()).getErrors());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ValidationExceptionResponse(Map.of("Error", List.of(e.getMessage()))));
        }
        return ResponseEntity.ok().build();
    }
}
