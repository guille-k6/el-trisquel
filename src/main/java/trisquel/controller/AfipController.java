package trisquel.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import trisquel.afip.model.*;
import trisquel.afip.model.DTO.*;
import trisquel.afip.service.InvoiceProcessingService;
import trisquel.afip.service.WsaaService;
import trisquel.model.Dto.DefaultList;

@RestController
@RequestMapping("/afip")
public class AfipController {
    InvoiceProcessingService invoiceProcessingService;
    WsaaService wsaaService;

    public AfipController(InvoiceProcessingService invoiceProcessingService, WsaaService wsaaService) {
        this.invoiceProcessingService = invoiceProcessingService;
        this.wsaaService = wsaaService;
    }


    @GetMapping("process-queued")
    public ResponseEntity processQueued() {
        try {
            invoiceProcessingService.processQueuedInvoices();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("last-cbte")
    public ResponseEntity testLastCBTE() throws Exception {
        try {
            AfipAuth auth = wsaaService.autenticar();
            Long response = invoiceProcessingService.getLastAuthorizedComprobante(auth, 2L, AfipComprobante.FACT_A);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tipo-comprobante")
    public ResponseEntity<DefaultList<?>> tiposCompobante() {
        DefaultList<AfipComprobanteDTO> defaultList = DefaultList.buildDefaultListFromEnum(AfipComprobante.FACT_A, AfipComprobanteDTO::fromEnum);
        return ResponseEntity.ok(defaultList);
    }

    @GetMapping("/moneda")
    public ResponseEntity<DefaultList<?>> monedas() {
        DefaultList<AfipMonedaDTO> defaultList = DefaultList.buildDefaultListFromEnum(AfipMoneda.PESO, AfipMonedaDTO::fromEnum);
        return ResponseEntity.ok(defaultList);
    }

    @GetMapping("/concepto")
    public ResponseEntity<DefaultList<?>> conceptos() {
        DefaultList<AfipConceptoDTO> defaultList = DefaultList.buildDefaultListFromEnum(AfipConcepto.PRODUCTO, AfipConceptoDTO::fromEnum);
        return ResponseEntity.ok(defaultList);
    }

    @GetMapping("/iva")
    public ResponseEntity<DefaultList<?>> ivas() {
        DefaultList<AfipIvaDTO> defaultList = DefaultList.buildDefaultListFromEnum(AfipIva.IVA_21p, AfipIvaDTO::fromEnum);
        return ResponseEntity.ok(defaultList);
    }

    @GetMapping("/tipo-documento")
    public ResponseEntity<DefaultList<?>> tiposDocumento() {
        DefaultList<AfipTipoDocDTO> defaultList = DefaultList.buildDefaultListFromEnum(AfipTipoDoc.DNI, AfipTipoDocDTO::fromEnum);
        return ResponseEntity.ok(defaultList);
    }

    @GetMapping("/condicion-iva")
    public ResponseEntity<DefaultList<?>> condicionesIva() {
        DefaultList<AfipCondicionIvaDTO> defaultList = DefaultList.buildDefaultListFromEnum(AfipCondicionIva.CONSUMIDOR_FINAL, AfipCondicionIvaDTO::fromEnum);
        return ResponseEntity.ok(defaultList);
    }


}
