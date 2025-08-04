package trisquel.Validators.Invoice;

import trisquel.Validators.Validator;
import trisquel.model.Dto.InvoiceInputDTO;
import trisquel.model.Product;
import trisquel.repository.ProductRepository;
import trisquel.utils.ValidationErrorItem;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProductExistenceInputValidator implements Validator<InvoiceInputDTO> {

    private final ProductRepository productRepository;

    public ProductExistenceInputValidator(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void validate(InvoiceInputDTO invoiceInputDTO, List<ValidationErrorItem> validationErrors) {
        if (invoiceInputDTO.getInvoiceItems() != null && !invoiceInputDTO.getInvoiceItems().isEmpty()) {
            Set<Long> productIds = invoiceInputDTO.getInvoiceItems().stream().map(ii -> ii.getProduct().getId()).filter(id -> id != null).collect(Collectors.toSet());

            if (!productIds.isEmpty()) {
                validateProductsExist(productIds, validationErrors);
            }
        }
    }

    private void validateProductsExist(Set<Long> productIds, List<ValidationErrorItem> validationErrors) {
        List<Product> products = productRepository.findByIdIn(productIds);
        List<Long> foundProductIds = products.stream().map(Product::getId).collect(Collectors.toList());

        if (products.size() != productIds.size()) {
            for (Long productId : productIds) {
                if (!foundProductIds.contains(productId)) {
                    validationErrors.add(new ValidationErrorItem("Error", "No se encontró un producto con el id: " + productId));
                    break; // Solo reportar el primer producto no encontrado
                }
            }
        }
    }
}
