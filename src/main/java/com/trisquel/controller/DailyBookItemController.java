package com.trisquel.controller;

import com.trisquel.model.DailyBookItem;
import com.trisquel.model.Dto.DailyBookItemDTO;
import com.trisquel.service.DailyBookItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/daily-book-item")
public class DailyBookItemController {

    private final DailyBookItemService dailyBookItemService;

    @Autowired
    public DailyBookItemController(DailyBookItemService dailyBookItemService) {
        this.dailyBookItemService = dailyBookItemService;
    }

    @GetMapping
    public List<DailyBookItem> getAllDailyBookItems() {
        return dailyBookItemService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DailyBookItem> getDailyBookItemById(@PathVariable Long id) {
        return dailyBookItemService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/latestVoucherNumber")
    public ResponseEntity<?> getLatestVoucherNumber() {
        Long number = dailyBookItemService.getHighestVoucherNumber();
        return ResponseEntity.ok(Map.of("latestVoucherNumber", number));
    }

    @GetMapping("/latestXVoucher")
    public ResponseEntity<?> getHighestXVoucherNumber() {
        String xVoucher = dailyBookItemService.getLatestXVoucherNumber();
        return ResponseEntity.ok(Map.of("latestXVoucher", xVoucher));
    }

    @PostMapping
    public ResponseEntity<DailyBookItem> createDailyBookItem(DailyBookItem dailyBookItem) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dailyBookItemService.save(dailyBookItem));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DailyBookItem> updateDailyBookItem(@RequestBody DailyBookItem dailyBookItem) {
        return ResponseEntity.ok(dailyBookItemService.save(dailyBookItem));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDailyBookItem(@PathVariable Long id) {
        dailyBookItemService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/invoiceableDailyBookItems")
    public ResponseEntity<Page<DailyBookItemDTO>> getInvoiceableDailyBookItems(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "30") int size,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"));
        Page<DailyBookItemDTO> items = dailyBookItemService.findInvoiceableDailyBookItems(pageable, clientId, startDate, endDate);
        return ResponseEntity.ok(items);
    }
}
