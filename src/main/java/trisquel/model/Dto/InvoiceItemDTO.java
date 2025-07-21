package trisquel.model.Dto;

import trisquel.afip.model.DTO.AfipIvaDTO;
import trisquel.model.InvoiceItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class InvoiceItemDTO {
    private Long id;
    private AfipIvaDTO iva;
    private Long invoiceId;
    private Long productId;
    private BigDecimal pricePerUnit;
    private BigDecimal ivaAmount;
    private BigDecimal total;


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
        invoiceItemDTO.setPricePerUnit(invoiceItem.getPricePerUnit());
        invoiceItemDTO.setIvaAmount(invoiceItem.getIvaAmount());
        invoiceItemDTO.setTotal(invoiceItem.getTotal());
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

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(BigDecimal pricePerUnit) {
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

    public BigDecimal getIvaAmount() {
        return ivaAmount;
    }

    public void setIvaAmount(BigDecimal ivaAmount) {
        this.ivaAmount = ivaAmount;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
