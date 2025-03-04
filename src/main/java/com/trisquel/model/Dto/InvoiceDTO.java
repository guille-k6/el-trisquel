package com.trisquel.model.Dto;

import java.sql.Date;

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
