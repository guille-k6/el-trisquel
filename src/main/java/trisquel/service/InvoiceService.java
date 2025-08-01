package trisquel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trisquel.afip.model.AfipComprobante;
import trisquel.afip.model.AfipConcepto;
import trisquel.afip.model.AfipMoneda;
import trisquel.model.*;
import trisquel.model.Dto.InvoiceDTO;
import trisquel.model.Dto.InvoiceInputDTO;
import trisquel.repository.*;
import trisquel.utils.ValidationErrorItem;
import trisquel.utils.ValidationException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    public List<InvoiceDTO> findAll() {
        List<Invoice> invoices = repository.findAll();
        return InvoiceDTO.translateToDTOs(invoices);
    }

    public Optional<Invoice> findById(Long id) {
        return repository.findById(id);
    }

    public Invoice save(Invoice invoice) {
        return repository.save(invoice);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    /**
     * Creates an invoice with its items, validates them and create an invoice queue request.
     *
     * @param invoiceInputDTO
     */
    @Transactional(rollbackFor = Throwable.class)
    public void processNewInvoiceRequest(InvoiceInputDTO invoiceInputDTO) {
        try {
            basicInvoiceValidation(invoiceInputDTO);
            List<DailyBookItem> dbis = dailyBookItemRepository.findByIdIn(invoiceInputDTO.getDbiIds());
            validateInvoiceWithDailyBookItemClients(invoiceInputDTO, dbis);
            Client client = clientRepository.findById(invoiceInputDTO.getClientId()).orElseThrow(() -> new ValidationException().addValidationError("Cliente no encontrado", "No se encontró un cliente con el ID: " + invoiceInputDTO.getClientId()));
            // Invoice creation
            Invoice invoice = createInvoiceShell(invoiceInputDTO, client);
            Invoice savedInvoice = repository.save(invoice);
            List<InvoiceItem> processedItems = processInvoiceItems(invoiceInputDTO.getInvoiceItems(), savedInvoice.getId());
            savedInvoice.setItems(processedItems);
            // InvoicePricing invoicePricing = new InvoicePricing(invoice);
            updateDailyBookItemsInvoices(dbis, savedInvoice.getId());
            generateInvoiceQueue(savedInvoice.getId());
        } catch (Throwable t) {
            System.out.println(t);
            throw t;
        }
    }

    private void generateInvoiceQueue(Long invoiceId) {
        InvoiceQueue invoiceQueue = new InvoiceQueue(invoiceId);
        invoiceQueueRepository.save(invoiceQueue);
    }

    /**
     * Basic fields validations. Verifies that the products of the invoice exists in our system.
     *
     * @param invoiceInputDTO new invoice request
     * @throws ValidationException if those conditions are true
     */
    public void basicInvoiceValidation(InvoiceInputDTO invoiceInputDTO) {
        List<ValidationErrorItem> validationErrors = new ArrayList<>();
        if (invoiceInputDTO.getInvoiceItems().isEmpty()) {
            validationErrors.add(new ValidationErrorItem("Error", "La factura debe tener al menos un item asociado"));
        }
        if (invoiceInputDTO.getDbiIds().isEmpty()) {
            validationErrors.add(new ValidationErrorItem("Error", "La factura debe tener al menos un item de libro diario asociado"));
        }
        if (invoiceInputDTO.getClientId() == null) {
            validationErrors.add(new ValidationErrorItem("Error", "La factura debe un cliente asociado"));
        }
        if (invoiceInputDTO.getInvoiceDate() == null) {
            validationErrors.add(new ValidationErrorItem("Error", "La factura debe tener fecha"));
        }
        Set<Long> productIds = new HashSet<>();
        for (InvoiceItem invoiceItem : invoiceInputDTO.getInvoiceItems()) {
            if (invoiceItem.getAmount() <= 0) {
                validationErrors.add(new ValidationErrorItem("Error", "La cantidad de los items de factura debe ser mayor que 0"));
            }
            if (invoiceItem.getPricePerUnit().compareTo(BigDecimal.ZERO) <= 0) {
                validationErrors.add(new ValidationErrorItem("Error", "La precio unitario de los items de factura debe ser mayor que 0"));
            }
            if (invoiceItem.getIva().getPercentage() < 0 || invoiceItem.getIva().getPercentage() > 100) {
                validationErrors.add(new ValidationErrorItem("Error", "El porcentaje de IVA debe ser mayor que 0 y menor que 100"));
            }
            productIds.add(invoiceItem.getProductId());
        }
        // First validation without DB queries
        ValidationException.verifyAndMaybeThrowValidationException(validationErrors);
        // Products validation
        List<Product> products = productRepository.findByIdIn(productIds);
        List<Long> foundProductIds = products.stream().map(Product::getId).collect(Collectors.toList());
        if (products.size() != productIds.size()) {
            // There is a product ID that is not in DB.
            for (Long productId : productIds) {
                if (!foundProductIds.contains(productId)) {
                    validationErrors.add(new ValidationErrorItem("Error", "No se encontró un producto con el id: " + productId));
                    break;
                }
            }
        }
        ValidationException.verifyAndMaybeThrowValidationException(validationErrors);
    }

    /**
     * Verifies that all the daily book items are from the same Client and that are not associated to any invoice previously
     *
     * @param invoiceInputDTO new invoice request
     * @param dbis            list of daily book items
     * @throws ValidationException if those conditions are true
     */
    private void validateInvoiceWithDailyBookItemClients(InvoiceInputDTO invoiceInputDTO, List<DailyBookItem> dbis) {
        List<ValidationErrorItem> validationErrors = new ArrayList<>();
        for (DailyBookItem dailyBookItem : dbis) {
            if (!dailyBookItem.getClient().getId().equals(invoiceInputDTO.getClientId())) {
                validationErrors.add(new ValidationErrorItem("Error", "Los items son de clientes diferentes." + dailyBookItem.getClient().getId() + " es distinto a: " + dailyBookItem.getClient().getName()));
            }
            if (dailyBookItem.getInvoiceId() != null) {
                validationErrors.add(new ValidationErrorItem("Error", "El item de libro diario: " + dailyBookItem.getId() + " ya tiene una factura asociada, la: " + dailyBookItem.getInvoiceId()));
            }
        }
        ValidationException.verifyAndMaybeThrowValidationException(validationErrors);
    }

    private Invoice createInvoiceShell(InvoiceInputDTO dto, Client client) {
        Invoice invoice = new Invoice();
        invoice.setDate(dto.getInvoiceDate());
        invoice.setClient(client);
        invoice.setCreatedAt(OffsetDateTime.now());
        invoice.setPaid(false);
        invoice.setStatus(InvoiceQueueStatus.QUEUED);
        invoice.setTotal(0.0);
        invoice.setComprobante(AfipComprobante.FACT_A);
        invoice.setConcepto(AfipConcepto.PRODUCTO);
        invoice.setMoneda(AfipMoneda.PESO);
        invoice.setSellPoint(1L);
        return invoice;
    }

    private List<InvoiceItem> processInvoiceItems(List<InvoiceItem> items, Long invoiceId) {
        for (InvoiceItem item : items) {
            item.setInvoice(new Invoice(invoiceId));
            item.setId(null);
            BigDecimal ivaAmount = item.getPricePerUnit().multiply(BigDecimal.valueOf(item.getAmount())).multiply(BigDecimal.valueOf(item.getIva().getPercentage())).divide(new BigDecimal(100));
            item.setIvaAmount(ivaAmount);
            BigDecimal total = item.getPricePerUnit().multiply(BigDecimal.valueOf(item.getAmount())).add(ivaAmount);
            item.setTotal(total);
        }
        invoiceItemRepository.saveAll(items);
        return items;
    }

    /**
     * Updates the daily book items with the id of the created invoice
     *
     * @param items     list of daily book items
     * @param invoiceId Invoice id of which these daily book items are associated
     */
    private void updateDailyBookItemsInvoices(List<DailyBookItem> items, Long invoiceId) {
        for (DailyBookItem dbi : items) {
            dbi.setInvoice(invoiceId);
        }
        dailyBookItemRepository.saveAll(items);
    }
}
