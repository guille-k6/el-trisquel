package com.trisquel.service;

import com.trisquel.model.*;
import com.trisquel.model.Dto.InvoiceInputDTO;
import com.trisquel.repository.*;
import com.trisquel.utils.ValidationErrorItem;
import com.trisquel.utils.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InvoiceService {
    @Autowired
    public InvoiceService(InvoiceRepository invoiceRepository, InvoiceItemRepository invoiceItemRepository,
                          DailyBookItemRepository dailyBookItemRepository, DailyBookRepository dailyBookRepository,
                          ClientRepository clientRepository, ProductRepository productRepository) {
        this.repository = invoiceRepository;
        this.invoiceItemRepository = invoiceItemRepository;
        this.dailyBookItemRepository = dailyBookItemRepository;
        this.dailyBookRepository = dailyBookRepository;
        this.clientRepository = clientRepository;
        this.productRepository = productRepository;
    }

    private final InvoiceRepository repository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final DailyBookItemRepository dailyBookItemRepository;
    private final DailyBookRepository dailyBookRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;

    public List<Invoice> findAll() {
        return repository.findAll();
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

    @Transactional(rollbackFor = Throwable.class)
    public void processNewInvoiceRequest(InvoiceInputDTO invoiceInputDTO) {
        try {
            basicInvoiceValidation(invoiceInputDTO);
            List<DailyBookItem> dbis = dailyBookItemRepository.findByIdIn(invoiceInputDTO.getDbiIds());
            validateInvoiceWithDailyBookItemClients(invoiceInputDTO, dbis);
            Client client = clientRepository.findById(invoiceInputDTO.getClientId()).orElseThrow(() -> new ValidationException().addValidationError("Cliente no encontrado", "No se encontró un cliente con el ID: " + invoiceInputDTO.getClientId()));
            //
            Invoice invoice = createInvoiceShell(invoiceInputDTO, client);
            // Guardar la factura primero para obtener el ID
            Invoice savedInvoice = repository.save(invoice);
            // Configurar y guardar los items de la factura
            processInvoiceItems(invoiceInputDTO.getInvoiceItems(), savedInvoice.getId());
            // Calcular y actualizar el total de la factura
            updateInvoiceTotal(savedInvoice);
            // Actualizar los items de libros diarios con el id de factura creada
            updateDailyBookItemsInvoices(dbis, savedInvoice.getId());
        } catch (Throwable t) {
            System.out.println(t);
            throw t;
        }
    }

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
            if (invoiceItem.getPricePerUnit() <= 0) {
                validationErrors.add(new ValidationErrorItem("Error", "La precio unitario de los items de factura debe ser mayor que 0"));
            }
            if (invoiceItem.getIvaPercentage() < 0 || invoiceItem.getIvaPercentage() > 100) {
                validationErrors.add(new ValidationErrorItem("Error", "El porcentaje de IVA debe ser mayor que 0 y menor que 100"));
            }
            productIds.add(invoiceItem.getProductId());
        }
        // Primera validación sin realizar consultas a base de datos
        ValidationException.verifyAndMaybeThrowValidationException(validationErrors);
        // Validacion de que existan los productos.
        List<Product> products = productRepository.findByIdIn(productIds);
        List<Long> foundProductIds = products.stream().map(Product::getId).collect(Collectors.toList());
        if (products.size() != productIds.size()) {
            // Hay un ID de producto que no lo encontró en base de datos
            for (Long productId : productIds) {
                if (!foundProductIds.contains(productId)) {
                    validationErrors.add(new ValidationErrorItem("Error", "No se encontró un producto con el id: " + productId));
                    break;
                }
            }
        }
        ValidationException.verifyAndMaybeThrowValidationException(validationErrors);
    }

    private void validateInvoiceWithDailyBookItemClients(InvoiceInputDTO invoiceInputDTO, List<DailyBookItem> dbis) {
        for (DailyBookItem dailyBookItem : dbis) {
            if (!dailyBookItem.getClient().getId().equals(invoiceInputDTO.getClientId())) {
                ValidationException validationException = new ValidationException();
                validationException.addValidationError("Error", "Los items son de clientes diferentes." + dailyBookItem.getClient().getId() + " es distinto a: " + dailyBookItem.getClient().getName());
            }
            if (dailyBookItem.getInvoiceId() != null) {
                ValidationException validationException = new ValidationException();
                validationException.addValidationError("Error", "El item de libro diario: " + dailyBookItem.getId() + " ya tiene una factura asociada, la: " + dailyBookItem.getInvoiceId());
            }
        }
    }

    private Invoice createInvoiceShell(InvoiceInputDTO dto, Client client) {
        Invoice invoice = new Invoice();
        invoice.setDate(dto.getInvoiceDate());
        invoice.setClient(client);
        invoice.setCreatedAt(OffsetDateTime.now());
        invoice.setPaid(false); // Por defecto no pagada
        invoice.setStatus("PENDING"); // Estado inicial
        invoice.setTotal(0.0); // Se calculará después
        return invoice;
    }

    private void processInvoiceItems(List<InvoiceItem> items, Long invoiceId) {
        for (InvoiceItem item : items) {
            item.setInvoiceId(invoiceId);
            // El ID del item se generará automáticamente
            item.setId(null);
        }
        // Guardar todos los items
        invoiceItemRepository.saveAll(items);
    }

    private void updateInvoiceTotal(Invoice invoice) {
        List<InvoiceItem> items = invoiceItemRepository.findByInvoiceId(invoice.getId());
        double total = items.stream().mapToDouble(item -> {
            double subtotal = item.getAmount() * item.getPricePerUnit();
            double iva = subtotal * (item.getIvaPercentage() / 100.0);
            return subtotal + iva;
        }).sum();
        invoice.setTotal(total);
        repository.save(invoice);
    }

    private void updateDailyBookItemsInvoices(List<DailyBookItem> items, Long invoiceId) {
        for (DailyBookItem dbi : items) {
            dbi.setInvoice(invoiceId);
        }
        dailyBookItemRepository.saveAll(items);
    }
}
