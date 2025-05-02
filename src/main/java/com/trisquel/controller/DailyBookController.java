package com.trisquel.controller;

import com.trisquel.model.DailyBook;
import com.trisquel.model.Dto.DailyBookDTO;
import com.trisquel.service.DailyBookService;
import com.trisquel.utils.ValidationException;
import com.trisquel.utils.ValidationExceptionResponse;
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
    public ResponseEntity<?> createDailyBook(@RequestBody DailyBook dailyBook) {
        ResponseEntity<?> response;
        try {
            dailyBookService.save(dailyBook);
            response = ResponseEntity.ok("");
        } catch (ValidationException e) {
            response = ResponseEntity.status(HttpStatus.CONFLICT).body(new ValidationExceptionResponse(e.getValidationErrors()).getErrors());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return response;
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

