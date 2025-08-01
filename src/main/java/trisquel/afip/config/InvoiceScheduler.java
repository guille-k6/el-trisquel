package trisquel.afip.config;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

/**
 * Cada 30 minutos:
 * 1. Scheduler se activa automáticamente
 * 2. Busca facturas QUEUED → STARTED
 * 3. Busca facturas FAILING → RETRYING
 * 4. Procesa cada factura con AFIP
 * 5. Actualiza estados: COMPLETED/FAILED
 * 6. Log completo del proceso
 */
@Component
@EnableAsync
public class InvoiceScheduler {

//    private final InvoiceProcessingService invoiceProcessingService;
    //    private final TaskExecutor taskExecutor;
    //
    //    public InvoiceScheduler(InvoiceProcessingService invoiceProcessingService,
    //                            @Qualifier("invoiceTaskExecutor") TaskExecutor taskExecutor) {
    //        this.invoiceProcessingService = invoiceProcessingService;
    //        this.taskExecutor = taskExecutor;
    //    }
    //
    //    @Scheduled(fixedRate = 30 * 60 * 1000) // 30 minutos en milisegundos
    //    @SchedulerLock(name = "processInvoiceQueue", lockAtMostFor = "25m", lockAtLeastFor = "1m")
    //    public void processInvoiceQueue() {
    //        // log.info("=== INICIANDO PROCESAMIENTO AUTOMÁTICO DE FACTURAS ===");
    //        // log.info("Timestamp: {}", ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
    //
    //        try {
    //            // Ejecutar de forma asíncrona para no bloquear el scheduler
    //            CompletableFuture.runAsync(() -> {
    //                try {
    //                    // TODO: Cambiar aca
    //                    // invoiceProcessingService.processQueuedInvoices();
    //                    // log.info("=== PROCESAMIENTO AUTOMÁTICO COMPLETADO EXITOSAMENTE ===");
    //                } catch (Exception e) {
    //                    // log.error("=== ERROR EN PROCESAMIENTO AUTOMÁTICO ===", e);
    //                }
    //            }, taskExecutor);
    //
    //        } catch (Exception e) {
    //            // log.error("Error iniciando procesamiento automático de facturas", e);
    //        }
    //    }
    //
    //    // Metodo para procesar manualmente (útil para testing o intervención manual)
    //    @Async("invoiceTaskExecutor")
    //    public CompletableFuture<Void> processInvoiceQueueManually() {
    //        // log.info("=== PROCESAMIENTO MANUAL INICIADO ===");
    //        try {
    //            invoiceProcessingService.processQueuedInvoices();
    //            // log.info("=== PROCESAMIENTO MANUAL COMPLETADO ===");
    //            return CompletableFuture.completedFuture(null);
    //        } catch (Exception e) {
    //            // log.error("Error en procesamiento manual", e);
    //            return CompletableFuture.failedFuture(e);
    //        }
    //    }
}