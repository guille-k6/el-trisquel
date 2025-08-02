package trisquel.Validators.Invoice;

import trisquel.Validators.Validator;
import trisquel.model.DailyBookItem;
import trisquel.model.Dto.InvoiceInputDTO;
import trisquel.repository.DailyBookItemRepository;
import trisquel.utils.ValidationErrorItem;

import java.util.List;

public class DbiIdsInputValidator implements Validator<InvoiceInputDTO> {

    private final DailyBookItemRepository dbiRepository;

    public DbiIdsInputValidator(DailyBookItemRepository dbiRepository) {
        this.dbiRepository = dbiRepository;
    }

    @Override
    public void validate(InvoiceInputDTO invoiceInputDTO, List<ValidationErrorItem> validationErrors) {
        if (invoiceInputDTO.getDbiIds() != null) {
            validateDbiIdsExist(invoiceInputDTO.getDbiIds(), validationErrors);
            validateDbiIds(invoiceInputDTO, validationErrors);
        }
    }

    private void validateDbiIdsExist(List<Long> dbiIds, List<ValidationErrorItem> validationErrors) {
        // Validaciones básicas de los DBI IDs
        for (Long dbiId : dbiIds) {
            if (dbiId == null || dbiId <= 0) {
                validationErrors.add(new ValidationErrorItem("Error", "Los IDs de libro diario deben ser valores positivos válidos"));
                break;
            }
        }
    }

    private void validateDbiIds(InvoiceInputDTO invoiceInputDTO, List<ValidationErrorItem> validationErrors) {
        List<DailyBookItem> dbis = dbiRepository.findByIdIn(invoiceInputDTO.getDbiIds());
        if (invoiceInputDTO.getDbiIds().size() != dbis.size()) {
            validationErrors.add(new ValidationErrorItem("Error", "Uno o más items de libro diario no existen"));
        }
        for (DailyBookItem dailyBookItem : dbis) {
            if (!dailyBookItem.getClient().getId().equals(invoiceInputDTO.getClientId())) {
                validationErrors.add(new ValidationErrorItem("Error", "Los items son de clientes diferentes." + dailyBookItem.getClient().getId() + " es distinto a: " + dailyBookItem.getClient().getName()));
            }
            if (dailyBookItem.getInvoiceId() != null) {
                validationErrors.add(new ValidationErrorItem("Error", "El item de libro diario: " + dailyBookItem.getId() + " ya tiene una factura asociada, la: " + dailyBookItem.getInvoiceId()));
            }
        }
    }
}
