package trisquel.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trisquel.model.ConfigurationMap;
import trisquel.model.DailyBookItem;
import trisquel.model.Product;
import trisquel.repository.DailyBookItemRepository;
import trisquel.repository.ProductRepository;
import trisquel.utils.ValidationErrorItem;
import trisquel.utils.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    public ProductService(ProductRepository repository, DailyBookItemRepository dailyBookItemRepository,
                          ConfigurationService configurationService) {
        this.repository = repository;
        this.dailyBookItemRepository = dailyBookItemRepository;
        this.configurationService = configurationService;
    }

    private final ProductRepository repository;
    private final DailyBookItemRepository dailyBookItemRepository;
    private final ConfigurationService configurationService;

    public List<Product> findAll() {
        return repository.findAll();
    }

    public Optional<Product> findById(Long id) {
        return repository.findById(id);
    }

    public Product save(Product product) {
        validateProduct(product);
        return repository.save(product);
    }

    private void validateProduct(Product product) {
        List<ValidationErrorItem> validationErrors = new ArrayList<>();
        if (product.getId() == null || product.getId() == 0) {
            product.setId(null);
        } else {
            // This is an update, verify the entity exists
            Optional<Product> existingProduct = repository.findById(product.getId());
            if (existingProduct.isEmpty()) {
                ValidationException validationException = new ValidationException();
                validationException.addValidationError("Error", "Producto no encontrado");
                throw validationException;
            }
        }
        if (product.getName().isBlank()) {
            validationErrors.add(new ValidationErrorItem("Error", "El campo nombre es obligatorio"));
        }
        ValidationException.verifyAndMaybeThrowValidationException(validationErrors);
    }

    public void delete(Long id) {
        List<DailyBookItem> itemsReferred = dailyBookItemRepository.findByProduct(id);
        if (!itemsReferred.isEmpty()) {
            Set<Long> idsReferred = itemsReferred.stream().map(dbi -> dbi.getDailyBook().getId()).collect(Collectors.toSet());
            ValidationException validationException = new ValidationException();
            validationException.addValidationError("Error", "El producto está siendo referenciado en los libros diarios: " + idsReferred);
            throw validationException;
        }
        repository.deleteById(id);
    }

    public Optional<Product> getDefaultProduct() {
        Optional<ConfigurationMap> defaultEntities = configurationService.findByKey("default");
        if (defaultEntities.isEmpty()) {
            return Optional.empty();
        }
        JsonNode defaultProduct = defaultEntities.get().getValue().get("productoDefault");
        if (defaultProduct == null) {
            return Optional.empty();
        }
        Product product = new Product();
        product.setId(defaultProduct.get("id").asLong());
        product.setName(defaultProduct.get("name").asText());
        return Optional.of(product);
    }
}
