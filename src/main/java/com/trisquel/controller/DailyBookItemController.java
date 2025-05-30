package com.trisquel.controller;

import com.trisquel.model.DailyBookItem;
import com.trisquel.service.DailyBookItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Long> getLatestVoucherNumber() {
        return ResponseEntity.ok(dailyBookItemService.getHighestVoucherNumber());
    }

    @GetMapping("/latestXVoucher")
    public ResponseEntity<String> getHighestXVoucherNumber() {
        return ResponseEntity.ok(dailyBookItemService.getLatestXVoucherNumber());
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
}
