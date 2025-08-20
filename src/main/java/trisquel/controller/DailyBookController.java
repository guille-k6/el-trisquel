package trisquel.controller;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import trisquel.model.DailyBook;
import trisquel.model.Dto.DailyBookDTO;
import trisquel.model.Dto.InvoiceDTO;
import trisquel.model.InvoiceQueueStatus;
import trisquel.service.DailyBookService;
import trisquel.utils.ValidationException;
import trisquel.utils.ValidationExceptionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/daily-book")
public class DailyBookController {

    private final DailyBookService dailyBookService;

    @Autowired
    public DailyBookController(DailyBookService dailyBookService) {
        this.dailyBookService = dailyBookService;
    }

    @GetMapping
    public ResponseEntity<?> getAllDailyBooks(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        ResponseEntity<?> response;
        try {
            Page<DailyBookDTO> dailyBooks = dailyBookService.findAll(page, dateFrom, dateTo);
            return ResponseEntity.ok(dailyBooks);
        } catch (ValidationException e) {
            response = ResponseEntity.status(HttpStatus.CONFLICT).body(new ValidationExceptionResponse(e.getValidationErrors()).getErrors());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ValidationExceptionResponse(Map.of("Error", List.of(e.getMessage()))));
        }
        return response;
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

