package com.trisquel.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "invoice")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoice_seq")
    @SequenceGenerator(name = "invoice_seq", sequenceName = "invoice_seq", allocationSize = 1)
    private Long id;

    private Date date;
    private Long amount;
    private Long pricePerUnit;
    private String comment;
    private Boolean paid;

    @OneToOne
    @JoinColumn(name = "dbi_id", referencedColumnName = "id")
    private DailyBookItem dailyBookItem;

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

    public DailyBookItem getDailyBookItem() {
        return dailyBookItem;
    }

    public void setDailyBookItem(DailyBookItem dailyBookItem) {
        this.dailyBookItem = dailyBookItem;
    }
}
