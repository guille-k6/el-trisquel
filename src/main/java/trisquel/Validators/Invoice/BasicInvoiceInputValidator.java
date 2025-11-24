package trisquel.Validators.Invoice;

import trisquel.Validators.Validator;
import trisquel.afip.model.SellCondition;
import trisquel.model.Dto.InvoiceInputDTO;
import trisquel.model.Dto.InvoiceItemDTO;
import trisquel.utils.ValidationErrorItem;

import java.time.LocalDate;
import java.util.List;

public class BasicInvoiceInputValidator implements Validator<InvoiceInputDTO> {

    @Override
    public void validate(InvoiceInputDTO invoiceInputDTO, List<ValidationErrorItem> validationErrors) {
        validateInvoiceDate(invoiceInputDTO.getInvoiceDate(), validationErrors);
        validateInvoiceSellCondition(invoiceInputDTO.getSellCondition(), validationErrors);
        validateClientId(invoiceInputDTO.getClientId(), validationErrors);
        validateInvoiceItems(invoiceInputDTO.getInvoiceItems(), validationErrors);
        validateDbiIds(invoiceInputDTO.getDbiIds(), validationErrors);
    }

    private void validateInvoiceDate(LocalDate invoiceDate, List<ValidationErrorItem> validationErrors) {
        if (invoiceDate == null) {
            validationErrors.add(new ValidationErrorItem("Error", "La factura debe tener fecha"));
        }
    }

    private void validateInvoiceSellCondition(SellCondition sellCondition, List<ValidationErrorItem> validationErrors) {
        if (sellCondition == null) {
            validationErrors.add(new ValidationErrorItem("Error", "La debe tener una condición de venta"));
        }
    }

    private void validateClientId(Long clientId, List<ValidationErrorItem> validationErrors) {
        if (clientId == null) {
            validationErrors.add(new ValidationErrorItem("Error", "La factura debe un cliente asociado"));
        }
    }

    private void validateInvoiceItems(List<InvoiceItemDTO> invoiceItems, List<ValidationErrorItem> validationErrors) {
        if (invoiceItems == null || invoiceItems.isEmpty()) {
            validationErrors.add(new ValidationErrorItem("Error", "La factura debe tener al menos un item asociado"));
        }
    }

    private void validateDbiIds(List<Long> dbiIds, List<ValidationErrorItem> validationErrors) {
        if (dbiIds == null || dbiIds.isEmpty()) {
            validationErrors.add(new ValidationErrorItem("Error", "La factura debe tener al menos un item de libro diario asociado"));
        }
    }
}
