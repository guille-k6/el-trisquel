package com.trisquel.model.Dto;

import com.trisquel.model.InvoiceItem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InvoiceInputDTO {
    LocalDate invoiceDate;
    Long clientId;
    List<Long> dbiIds = new ArrayList<>();
    List<InvoiceItem> invoiceItems = new ArrayList<>();

    public InvoiceInputDTO() {
    }

    public InvoiceInputDTO(LocalDate invoiceDate, Long clientId, List<Long> dbiIds, List<InvoiceItem> invoiceItems) {
        this.invoiceDate = invoiceDate;
        this.clientId = clientId;
        this.dbiIds = dbiIds;
        this.invoiceItems = invoiceItems;
    }

    public List<Long> getDbiIds() {
        return dbiIds;
    }

    public void setDbiIds(List<Long> dbiIds) {
        this.dbiIds = dbiIds;
    }

    public List<InvoiceItem> getInvoiceItems() {
        return invoiceItems;
    }

    public void setInvoiceItems(List<InvoiceItem> invoiceItems) {
        this.invoiceItems = invoiceItems;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}
