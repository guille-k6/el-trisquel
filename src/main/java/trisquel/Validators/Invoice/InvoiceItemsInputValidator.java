package trisquel.Validators.Invoice;

import trisquel.Validators.Validator;
import trisquel.afip.model.AfipIva;
import trisquel.model.Dto.InvoiceInputDTO;
import trisquel.model.InvoiceItem;
import trisquel.utils.ValidationErrorItem;

import java.math.BigDecimal;
import java.util.List;

public class InvoiceItemsInputValidator implements Validator<InvoiceInputDTO> {

    @Override
    public void validate(InvoiceInputDTO invoiceInputDTO, List<ValidationErrorItem> validationErrors) {
        if (invoiceInputDTO.getInvoiceItems() != null) {
            for (InvoiceItem item : invoiceInputDTO.getInvoiceItems()) {
                validateItem(item, validationErrors);
            }
        }
    }

    private void validateItem(InvoiceItem item, List<ValidationErrorItem> validationErrors) {
        validateAmount(item.getAmount(), validationErrors);
        validatePricePerUnit(item.getPricePerUnit(), validationErrors);
        validateIva(item.getIva(), validationErrors);
        validateProductId(item.getProduct().getId(), validationErrors);
    }

    private void validateAmount(Integer amount, List<ValidationErrorItem> validationErrors) {
        if (amount == null || amount <= 0) {
            validationErrors.add(new ValidationErrorItem("Error", "La cantidad de los items de factura debe ser mayor que 0"));
        }
    }

    private void validatePricePerUnit(BigDecimal pricePerUnit, List<ValidationErrorItem> validationErrors) {
        if (pricePerUnit == null || pricePerUnit.compareTo(BigDecimal.ZERO) <= 0) {
            validationErrors.add(new ValidationErrorItem("Error", "El precio unitario de los items de factura debe ser mayor que 0"));
        }
    }

    private void validateIva(AfipIva iva, List<ValidationErrorItem> validationErrors) {
        if (iva == null) {
            validationErrors.add(new ValidationErrorItem("Error", "El item debe tener una alícuota de IVA"));
        }
    }

    private void validateProductId(Long productId, List<ValidationErrorItem> validationErrors) {
        if (productId == null) {
            validationErrors.add(new ValidationErrorItem("Error", "El item debe tener un producto asociado"));
        }
    }
}
