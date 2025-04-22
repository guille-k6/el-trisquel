package com.trisquel.controller;

import com.trisquel.model.DailyBook;
import com.trisquel.model.Dto.DailyBookDTO;
import com.trisquel.service.DailyBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/daily-book")
public class DailyBookController {

    private final DailyBookService dailyBookService;

    @Autowired
    public DailyBookController(DailyBookService dailyBookService) {
        this.dailyBookService = dailyBookService;
    }

    @GetMapping
    public List<DailyBookDTO> getAllDailyBooks() {
        return dailyBookService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DailyBookDTO> getDailyBookById(@PathVariable Long id) {
        return dailyBookService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DailyBook> createDailyBook(@RequestBody DailyBook dailyBook) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dailyBookService.save(dailyBook));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DailyBook> updateDailyBook(@PathVariable Long id, @RequestBody DailyBook dailyBook) {
        return ResponseEntity.ok(dailyBookService.save(dailyBook));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDailyBook(@PathVariable Long id) {
        dailyBookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

