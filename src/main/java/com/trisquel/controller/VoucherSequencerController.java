package com.trisquel.controller;

import com.trisquel.model.VoucherSequencer;
import com.trisquel.service.VoucherSequencerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/voucher-sequencer")
public class VoucherSequencerController {

    private final VoucherSequencerService voucherSequencerService;

    @Autowired
    public VoucherSequencerController(VoucherSequencerService voucherSequencerService) {
        this.voucherSequencerService = voucherSequencerService;
    }

    /**
     * Get the current sequence number
     *
     * @return ResponseEntity with the current sequence value
     */
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentSequence() {
        Long currentSequence = voucherSequencerService.getCurrentSequence();

        if (currentSequence == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createResponse("Not found", "No sequence has been initialized yet"));
        }

        return ResponseEntity.ok(createResponse("Current sequence", currentSequence));
    }

    /**
     * Set the sequence to a specific number
     *
     * @param number the number to set the sequence to
     * @return ResponseEntity with the updated sequence
     */
    @PostMapping("/set/{number}")
    public ResponseEntity<?> setSequence(@PathVariable Long number) {
        try {
            VoucherSequencer voucher = voucherSequencerService.setSequence(number);
            return ResponseEntity.ok(createResponse("Sequence set", voucher.getNumber()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createResponse("Error", e.getMessage()));
        }
    }

    /**
     * Helper method to create consistent response format
     *
     * @param message the message to include in the response
     * @param data    the data to include in the response
     * @return a map containing the message and data
     */
    private Map<String, Object> createResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("data", data);
        return response;
    }
}
