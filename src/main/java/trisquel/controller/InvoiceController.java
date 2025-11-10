package trisquel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trisquel.model.Dto.InvoiceDTO;
import trisquel.model.Dto.InvoiceInputDTO;
import trisquel.model.Dto.PricesSuggestionDTO;
import trisquel.model.InvoiceQueueStatus;
import trisquel.service.InvoiceService;
import trisquel.utils.ValidationException;
import trisquel.utils.ValidationExceptionResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;

    }

    @GetMapping
    public ResponseEntity<?> getAllInvoices(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
                                            @RequestParam(required = false) Long clientId,
                                            @RequestParam(required = false) InvoiceQueueStatus status) {
        ResponseEntity<?> response;
        try {
            Page<InvoiceDTO> invoices = invoiceService.findAll(page, dateFrom, dateTo, clientId, status);
            return ResponseEntity.ok(invoices);
        } catch (ValidationException e) {
            response = ResponseEntity.status(HttpStatus.CONFLICT).body(new ValidationExceptionResponse(e.getValidationErrors()).getErrors());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ValidationExceptionResponse(Map.of("Error", List.of(e.getMessage()))));
        }
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInvoiceById(@PathVariable Long id) {
        ResponseEntity<?> response;
        try {
            Optional<InvoiceDTO> invoice = invoiceService.findById(id);
            if (invoice.isEmpty()) {
                ResponseEntity.notFound().build();
            }
            response = ResponseEntity.ok(invoice.get());
        } catch (ValidationException e) {
            response = ResponseEntity.status(HttpStatus.CONFLICT).body(new ValidationExceptionResponse(e.getValidationErrors()).getErrors());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ValidationExceptionResponse(Map.of("Error", List.of(e.getMessage()))));
        }
        return response;
    }

    @GetMapping("/total/{id}")
    public ResponseEntity<?> updateInvoiceTotal(@PathVariable Long id) {
        ResponseEntity<?> response;
        try {
            invoiceService.updateInvoiceTotal(id);
            response = ResponseEntity.ok().build();
        } catch (ValidationException e) {
            response = ResponseEntity.status(HttpStatus.CONFLICT).body(new ValidationExceptionResponse(e.getValidationErrors()).getErrors());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ValidationExceptionResponse(Map.of("Error", List.of(e.getMessage()))));
        }
        return response;
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

    @GetMapping("/suggest-prices/{clientId}/{productId}")
    public ResponseEntity<?> suggestPrices(@PathVariable Long clientId, @PathVariable Long productId) {
        ResponseEntity<?> response;
        try {
            PricesSuggestionDTO prices = invoiceService.suggestPrices(clientId, productId);
            response = ResponseEntity.ok(prices);
        } catch (ValidationException e) {
            response = ResponseEntity.status(HttpStatus.CONFLICT).body(new ValidationExceptionResponse(e.getValidationErrors()).getErrors());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ValidationExceptionResponse(Map.of("Error", List.of(e.getMessage()))));
        }
        return response;
    }
}
