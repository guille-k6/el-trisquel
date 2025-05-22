package com.trisquel.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "daily_book_item")
public class DailyBookItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "daily_book_item_seq")
    @SequenceGenerator(name = "daily_book_item_seq", sequenceName = "daily_book_item_seq", allocationSize = 1)
    private Long id;

    private Long amount;
    private Boolean authorized;
    @ManyToOne
    @JoinColumn(name = "daily_book_id", referencedColumnName = "id")
    private DailyBook dailyBook;

    @ManyToOne
    @JoinColumn(name = "invoice_id", referencedColumnName = "id")
    private Invoice invoice;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private Client client;


    private LocalDate date;

    private Long payment;

    private String observations;

    private String xVoucher;
    private Long voucherNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Boolean getAuthorized() {
        return authorized;
    }

    public void setAuthorized(Boolean authorized) {
        this.authorized = authorized;
    }

    public DailyBook getDailyBook() {
        return dailyBook;
    }

    public void setDailyBook(DailyBook dailyBook) {
        this.dailyBook = dailyBook;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getPayment() {
        return payment;
    }

    public void setPayment(Long payment) {
        this.payment = payment;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public String getXVoucher() {
        return xVoucher;
    }

    public void setXvoucher(String xvoucher) {
        this.xVoucher = xvoucher;
    }

    public Long getVoucherNumber() {
        return voucherNumber;
    }

    public void setVoucherNumber(Long voucherNumber) {
        this.voucherNumber = voucherNumber;
    }
}