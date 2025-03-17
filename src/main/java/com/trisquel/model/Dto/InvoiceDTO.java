package com.trisquel.model.Dto;

import com.trisquel.model.Invoice;

import java.util.Date;
import java.util.Optional;

public class InvoiceDTO {
    private Long id;
    private Date date;
    private Long amount;
    private Long pricePerUnit;
    private String comment;
    private Boolean paid;

    public InvoiceDTO(Long id, Date date, Long amount, Long pricePerUnit, String comment, Boolean paid) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.pricePerUnit = pricePerUnit;
        this.comment = comment;
        this.paid = paid;
    }

    public InvoiceDTO() {
    }

    public static Optional<InvoiceDTO> translateToDTO(Invoice invoice) {
        if (invoice == null) {
            return Optional.empty();
        }
        InvoiceDTO invoiceDTO = new InvoiceDTO();
        invoiceDTO.setId(invoice.getId());
        invoiceDTO.setAmount(invoice.getAmount());
        invoiceDTO.setComment(invoice.getComment());
        invoiceDTO.setDate(invoice.getDate());
        invoiceDTO.setPricePerUnit(invoice.getPricePerUnit());
        invoiceDTO.setPaid(invoice.getPaid());
        return Optional.of(invoiceDTO);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(Long pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }
}
