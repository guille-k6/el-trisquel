package trisquel.model;

import jakarta.persistence.*;

@Entity
@Table(name = "invoice_item")
public class InvoiceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoice_item_seq")
    @SequenceGenerator(name = "invoice_item_seq", sequenceName = "invoice_item_seq", allocationSize = 1)
    private Long id;
    private Integer amount;
    @Column(name = "price_per_unit")
    private Double pricePerUnit;
    @Column(name = "iva_percetage")
    private Integer ivaPercentage;
    @Column(name = "invoice_id", nullable = false)
    private Long invoiceId;
    @Column(name = "product_id", nullable = false)
    private Long productId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(Double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public Integer getIvaPercentage() {
        return ivaPercentage;
    }

    public void setIvaPercentage(Integer ivaPercentage) {
        this.ivaPercentage = ivaPercentage;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }
}