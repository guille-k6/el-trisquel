package com.trisquel.service;

import com.trisquel.model.DailyBookItem;
import com.trisquel.repository.DailyBookItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DailyBookItemService {
    @Autowired
    DailyBookItemService(DailyBookItemRepository repository) {
        this.repository = repository;
    }

    private final DailyBookItemRepository repository;

    public List<DailyBookItem> findAll() {
        return repository.findAll();
    }

    public Optional<DailyBookItem> findById(Long id) {
        return repository.findById(id);
    }

    public DailyBookItem save(DailyBookItem dailyBookItem) {
        return repository.save(dailyBookItem);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Long getHighestVoucherNumber() {
        Optional<DailyBookItem> dbi = repository.findHighestVoucherNumber();
        if (dbi.isEmpty()) {
            return 1L;
        }
        return dbi.get().getVoucherNumber();
    }

    public String getLatestXVoucherNumber() {
        Optional<DailyBookItem> dbi = repository.findLatestXVoucher();
        if (dbi.isEmpty()) {
            return "X-1";
        }
        return dbi.get().getXVoucher();
    }
}
