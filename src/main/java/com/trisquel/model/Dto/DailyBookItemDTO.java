package com.trisquel.model.Dto;

public class DailyBookItemDTO {
    private Long id;
    private Long invoiceId;
    private Long amount;
    private Long productId;
    private Boolean authorized;
    private Long clientId;

    public DailyBookItemDTO(Long id, Long invoiceId, Long amount, Long productId, Boolean authorized, Long clientId) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.amount = amount;
        this.productId = productId;
        this.authorized = authorized;
        this.clientId = clientId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Boolean getAuthorized() {
        return authorized;
    }

    public void setAuthorized(Boolean authorized) {
        this.authorized = authorized;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}
