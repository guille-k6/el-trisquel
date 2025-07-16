package trisquel.model.Dto;

import trisquel.afip.model.DTO.AfipIvaDTO;
import trisquel.model.InvoiceItem;

import java.util.List;
import java.util.stream.Collectors;

public class InvoiceItemDTO {
    private Long id;
    private Double pricePerUnit;
    private AfipIvaDTO iva;
    private Long invoiceId;
    private Long productId;

    InvoiceItemDTO() {
    }

    public static InvoiceItemDTO translateToDTO(InvoiceItem invoiceItem) {
        if (invoiceItem == null) {
            throw new IllegalArgumentException("Invoice item cannot be null");
        }
        InvoiceItemDTO invoiceItemDTO = new InvoiceItemDTO();
        invoiceItemDTO.setId(invoiceItem.getId());
        invoiceItemDTO.setPricePerUnit(invoiceItem.getPricePerUnit());
        invoiceItemDTO.setIva(AfipIvaDTO.fromEnum(invoiceItem.getIva()));
        invoiceItemDTO.setInvoiceId(invoiceItem.getInvoice().getId());
        invoiceItemDTO.setProductId(invoiceItem.getProductId());
        return invoiceItemDTO;
    }

    public static List<InvoiceItemDTO> translateToDTOs(List<InvoiceItem> invoices) {
        return invoices.stream().map(InvoiceItemDTO::translateToDTO).collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(Double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public AfipIvaDTO getIva() {
        return iva;
    }

    public void setIva(AfipIvaDTO iva) {
        this.iva = iva;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
