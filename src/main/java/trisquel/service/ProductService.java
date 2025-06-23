package trisquel.service;

import trisquel.model.Product;
import trisquel.repository.ProductRepository;
import trisquel.utils.ValidationErrorItem;
import trisquel.utils.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    private final ProductRepository repository;

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
        if (!validationErrors.isEmpty()) {
            ValidationException validationException = new ValidationException();
            for (ValidationErrorItem validationErrorItem : validationErrors) {
                validationException.addValidationError(validationErrorItem.title(), validationErrorItem.message());
            }
            throw validationException;
        }
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
