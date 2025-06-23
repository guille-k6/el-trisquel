package trisquel.controller;

import trisquel.afip.config.InvoiceScheduler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import trisquel.model.InvoiceQueueStatus;
import trisquel.repository.InvoiceQueueRepository;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/invoice-queue")
public class InvoiceQueueController {

    private final InvoiceScheduler invoiceScheduler;
    private final InvoiceQueueRepository invoiceQueueRepository;

    public InvoiceQueueController(InvoiceScheduler invoiceScheduler,
                                  InvoiceQueueRepository invoiceQueueRepository) {
        this.invoiceScheduler = invoiceScheduler;
        this.invoiceQueueRepository = invoiceQueueRepository;
    }

    @PostMapping("/process-queue")
    public ResponseEntity<String> processQueueManually() {
        try {
            invoiceScheduler.processInvoiceQueueManually();
            return ResponseEntity.ok("Procesamiento manual iniciado");
        } catch (Exception e) {
            // log.error("Error iniciando procesamiento manual", e);
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/queue/status")
    public ResponseEntity<Map<String, Object>> getQueueStatus() {
        try {
            List<InvoiceQueueStatus> allStatuses = Arrays.asList(InvoiceQueueStatus.values());
            Map<String, Long> statusCounts = new HashMap<>();

            for (InvoiceQueueStatus status : allStatuses) {
                long count = invoiceQueueRepository.countByStatus(status);
                statusCounts.put(status.name(), count);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("timestamp", ZonedDateTime.now());
            response.put("statusCounts", statusCounts);
            response.put("totalInQueue", statusCounts.get("QUEUED") + statusCounts.get("FAILING"));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // log.error("Error obteniendo estado de cola", e);
            return ResponseEntity.status(500).build();
        }
    }
}
