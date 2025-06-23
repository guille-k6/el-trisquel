package trisquel.controller;

import trisquel.model.Voucher;
import trisquel.service.VoucherService;
import trisquel.utils.ValidationException;
import trisquel.utils.ValidationExceptionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vouchers")
public class VoucherController {

    private final VoucherService voucherService;

    @Autowired
    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @GetMapping
    public List<Voucher> getAllVouchers() {
        return voucherService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Voucher> getVoucherById(@PathVariable Long id) {
        return voucherService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createVoucher(@RequestBody Voucher voucher) {
        ResponseEntity<?> response;
        try {
            voucherService.save(voucher);
            response = ResponseEntity.ok("");
        } catch (ValidationException e) {
            response = ResponseEntity.status(HttpStatus.CONFLICT).body(new ValidationExceptionResponse(e.getValidationErrors()).getErrors());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return response;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Voucher> updateVoucher(@RequestBody Voucher voucher) {
        return ResponseEntity.ok(voucherService.save(voucher));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVoucher(@PathVariable Long id) {
        voucherService.delete(id);
        return ResponseEntity.noContent().build();
    }
}