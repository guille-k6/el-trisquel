package com.trisquel.model.Dto;

import com.trisquel.model.Invoice;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class InvoiceDTO {
    private Long id;
    private LocalDate date;

    private String comment;

    private Boolean paid;

    private String status;

    private OffsetDateTime createdAt;

    public InvoiceDTO() {
    }

    public static InvoiceDTO translateToDTO(Invoice invoice) {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice cannot be null");
        }
        InvoiceDTO invoiceDTO = new InvoiceDTO();
        invoiceDTO.setId(invoice.getId());
        invoiceDTO.setDate(invoice.getDate());
        invoiceDTO.setComment(invoice.getComment());
        invoiceDTO.setPaid(invoice.getPaid());
        invoiceDTO.setStatus(invoice.getStatus());
        invoiceDTO.setCreatedAt(invoice.getCreatedAt());
        return invoiceDTO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
