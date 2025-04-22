package com.trisquel.service;

import com.trisquel.model.DailyBook;
import com.trisquel.model.Dto.DailyBookDTO;
import com.trisquel.repository.DailyBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DailyBookService {
    @Autowired
    public DailyBookService(DailyBookRepository repository) {
        this.repository = repository;
    }

    private final DailyBookRepository repository;

    public List<DailyBookDTO> findAll() {
        List<DailyBook> dailyBooks = repository.findAll();
        return DailyBookDTO.translateToDTOs(dailyBooks);
    }

    public Optional<DailyBookDTO> findById(Long id) {
        Optional<DailyBook> dailyBook = repository.findById(id);
        if (dailyBook.isPresent()) {
            DailyBookDTO dailyBookDTO = DailyBookDTO.translateToDTO(dailyBook.get());
            return Optional.of(dailyBookDTO);
        } else {
            return Optional.empty();
        }
    }

    public DailyBook save(DailyBook dailyBook) {
        return repository.save(dailyBook);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
