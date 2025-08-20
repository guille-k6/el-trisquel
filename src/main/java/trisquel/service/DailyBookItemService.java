package trisquel.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import trisquel.model.DailyBookItem;
import trisquel.model.Dto.DailyBookItemDTO;
import trisquel.repository.DailyBookItemRepository;
import trisquel.utils.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public Page<DailyBookItemDTO> findInvoiceableDailyBookItems(int page, Long clientId,
                                                                LocalDate startDate, LocalDate endDate) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "date"));
        Specification<DailyBookItem> spec = Specification.where(null);
        spec = spec.and((root, query, cb) -> cb.isNull(root.get("invoiceId")));
        if (startDate != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("date"), startDate));
        }
        if (endDate != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("date"), endDate));
        }
        if (clientId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("client").get("id"), clientId));
        }
        Page<DailyBookItem> dailyBookItemsPage = repository.findAll(spec, pageable);
        return dailyBookItemsPage.map(DailyBookItemDTO::translateToDTO);
    }

    public List<DailyBookItemDTO> getItemsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("IDs list cannot be null or empty");
        }
        // Remove duplicates and null values
        List<Long> cleanIds = ids.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (cleanIds.isEmpty()) {
            throw new IllegalArgumentException("No valid IDs provided");
        }
        List<DailyBookItem> items = repository.findByIdIn(cleanIds);
        // Optional: Check if all requested items were found
        if (items.size() != cleanIds.size()) {
            List<Long> foundIds = items.stream().map(DailyBookItem::getId).collect(Collectors.toList());
            List<Long> notFoundIds = cleanIds.stream().filter(id -> !foundIds.contains(id)).collect(Collectors.toList());
            // You can log this or handle it as needed
            System.out.println("Items not found for IDs: " + notFoundIds);
        }
        // All items should be from the same client, check that
        if (!items.isEmpty()) {
            Long firstClientId = items.getFirst().getClient().getId();
            for (DailyBookItem item : items) {
                if (!item.getClient().getId().equals(firstClientId)) {
                    ValidationException ve = new ValidationException();
                    ve.addValidationError("Error", "Los items son de clientes disintos");
                    throw ve;
                }
            }
        }
        return items.stream().map(DailyBookItemDTO::translateToDTO).collect(Collectors.toList());
    }
}
