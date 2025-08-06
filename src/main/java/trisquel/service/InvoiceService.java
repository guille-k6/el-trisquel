package trisquel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trisquel.Validators.Invoice.*;
import trisquel.Validators.Validator;
import trisquel.afip.model.AfipComprobante;
import trisquel.afip.model.AfipConcepto;
import trisquel.afip.model.AfipIva;
import trisquel.afip.model.AfipMoneda;
import trisquel.model.*;
import trisquel.model.Dto.InvoiceDTO;
import trisquel.model.Dto.InvoiceInputDTO;
import trisquel.model.Dto.InvoiceItemDTO;
import trisquel.repository.*;
import trisquel.utils.ValidationErrorItem;
import trisquel.utils.ValidationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    @Autowired
    public InvoiceService(InvoiceRepository invoiceRepository, InvoiceItemRepository invoiceItemRepository,
                          DailyBookItemRepository dailyBookItemRepository, DailyBookRepository dailyBookRepository,
                          ClientRepository clientRepository, ProductRepository productRepository,
                          InvoiceQueueRepository invoiceQueueRepository) {
        this.repository = invoiceRepository;
        this.invoiceItemRepository = invoiceItemRepository;
        this.dailyBookItemRepository = dailyBookItemRepository;
        this.dailyBookRepository = dailyBookRepository;
        this.clientRepository = clientRepository;
        this.productRepository = productRepository;
        this.invoiceQueueRepository = invoiceQueueRepository;
    }

    private final InvoiceRepository repository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final DailyBookItemRepository dailyBookItemRepository;
    private final DailyBookRepository dailyBookRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final InvoiceQueueRepository invoiceQueueRepository;

    private final Long SELL_POINT = 2L;

    public Page<InvoiceDTO> findAll(int page, LocalDate dateFrom, LocalDate dateTo, Long clientId,
                                    InvoiceQueueStatus status) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by("date").descending());
        Specification<Invoice> spec = Specification.where(null);
        if (dateFrom != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("date"), dateFrom));
        }
        if (dateTo != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("date"), dateTo));
        }
        if (clientId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("client").get("id"), clientId));
        }
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        Page<Invoice> invoicesPage = repository.findAll(spec, pageable);
        return invoicesPage.map(InvoiceDTO::translateToDTO);
    }

    public Optional<InvoiceDTO> findById(Long id) {
        Optional<Invoice> invoice = repository.findById(id);
        if (invoice.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(InvoiceDTO.translateToDTO(invoice.get()));
    }

    public Optional<Invoice> findInvoiceById(Long id) {
        Optional<Invoice> invoice = repository.findById(id);
        if (invoice.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(invoice.get());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void processNewInvoiceRequest(InvoiceInputDTO invoiceInputDTO) {
        try {
            validate(invoiceInputDTO);
            List<DailyBookItem> dbis = dailyBookItemRepository.findByIdIn(invoiceInputDTO.getDbiIds());
            Client client = clientRepository.findById(invoiceInputDTO.getClientId()).orElseThrow(() -> new ValidationException().addValidationError("Cliente no encontrado", "No se encontró un cliente con el ID: " + invoiceInputDTO.getClientId()));
            Invoice invoice = createInvoiceShell(invoiceInputDTO, client);
            Invoice savedInvoice = repository.save(invoice);
            List<InvoiceItem> processedItems = processInvoiceItems(invoiceInputDTO.getInvoiceItems(), savedInvoice);
            savedInvoice.setItems(processedItems);
            // InvoicePricing invoicePricing = new InvoicePricing(invoice);
            updateDailyBookItemsInvoices(dbis, savedInvoice.getId());
            invoiceQueueRepository.save(new InvoiceQueue(savedInvoice.getId()));
        } catch (Throwable t) {
            System.out.println(t);
            throw t;
        }
    }

    public void validate(InvoiceInputDTO invoiceInputDTO) {
        List<ValidationErrorItem> validationErrors = new ArrayList<>();
        List<Validator<InvoiceInputDTO>> basicValidators = Arrays.asList(new BasicInvoiceInputValidator(), new InvoiceItemsInputValidator());
        for (Validator<InvoiceInputDTO> validator : basicValidators) {
            validator.validate(invoiceInputDTO, validationErrors);
            ValidationException.verifyAndMaybeThrowValidationException(validationErrors);
        }
        // Validadores que requieren acceso a BD
        List<Validator<InvoiceInputDTO>> dbValidators = Arrays.asList(new ClientExistenceInputValidator(clientRepository), new ProductExistenceInputValidator(productRepository), new DbiIdsInputValidator(dailyBookItemRepository));
        for (Validator<InvoiceInputDTO> validator : dbValidators) {
            validator.validate(invoiceInputDTO, validationErrors);
            ValidationException.verifyAndMaybeThrowValidationException(validationErrors);
        }
    }

    private Invoice createInvoiceShell(InvoiceInputDTO dto, Client client) {
        Invoice invoice = new Invoice();
        invoice.setDate(dto.getInvoiceDate());
        invoice.setClient(client);
        invoice.setCreatedAt(OffsetDateTime.now());
        invoice.setPaid(false);
        invoice.setStatus(InvoiceQueueStatus.QUEUED);
        invoice.setTotal(BigDecimal.ZERO);
        invoice.setComprobante(AfipComprobante.FACT_A);
        invoice.setConcepto(AfipConcepto.PRODUCTO);
        invoice.setMoneda(AfipMoneda.PESO);
        invoice.setSellPoint(SELL_POINT);
        return invoice;
    }

    private List<InvoiceItem> processInvoiceItems(List<InvoiceItemDTO> dtoItems, Invoice invoice) {
        BigDecimal invoiceTotal = BigDecimal.ZERO;
        List<InvoiceItem> invoiceItems = new ArrayList<>();
        for (InvoiceItemDTO itemDTO : dtoItems) {
            InvoiceItem item = new InvoiceItem();
            item.setInvoice(new Invoice(invoice.getId()));
            item.setAmount(itemDTO.getAmount());
            item.setIva(AfipIva.fromCode(itemDTO.getIva().code()));
            item.setPricePerUnit(itemDTO.getPricePerUnit());
            BigDecimal ivaAmount = itemDTO.getPricePerUnit().multiply(BigDecimal.valueOf(itemDTO.getAmount())).multiply(BigDecimal.valueOf(itemDTO.getIva().percentage())).divide(new BigDecimal(100));
            item.setIvaAmount(ivaAmount);
            BigDecimal total = itemDTO.getPricePerUnit().multiply(BigDecimal.valueOf(itemDTO.getAmount())).add(ivaAmount);
            item.setTotal(total);
            item.setProduct(new Product(itemDTO.getProduct().getId()));
            invoiceItems.add(item);
            invoiceTotal = invoiceTotal.add(total);
        }
        invoiceItemRepository.saveAll(invoiceItems);
        invoice.setTotal(invoiceTotal);
        return invoiceItems;
    }

    private void updateDailyBookItemsInvoices(List<DailyBookItem> items, Long invoiceId) {
        for (DailyBookItem dbi : items) {
            dbi.setInvoice(invoiceId);
        }
        dailyBookItemRepository.saveAll(items);
    }

    @Transactional
    public void updateAfipFields(Invoice invoice, String cae, LocalDate vtoCae) {
        repository.updateAfipResponseFields(cae, vtoCae, invoice.getId());
    }

    @Transactional
    public void updateInvoiceStatus(Invoice invoice, InvoiceQueueStatus status) {
        repository.updateStatus(status, invoice.getId());
    }

    public void updateInvoiceTotal(Long invoiceId) {
        Optional<Invoice> invoice = repository.findById(invoiceId);
        BigDecimal invoiceTotal = BigDecimal.ZERO;
        for (InvoiceItem item : invoice.get().getItems()) {
            invoiceTotal = invoiceTotal.add(item.getTotal());
        }
        invoice.get().setTotal(invoiceTotal);
        repository.save(invoice.get());
    }
}
