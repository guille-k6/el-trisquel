package trisquel.model;

import jakarta.persistence.*;
import trisquel.afip.model.AfipIva;

import java.math.BigDecimal;

@Entity
@Table(name = "invoice_item")
public class InvoiceItem {
    public InvoiceItem() {
        
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoice_item_seq")
    @SequenceGenerator(name = "invoice_item_seq", sequenceName = "invoice_item_seq", allocationSize = 1)
    private Long id;
    private Integer amount;
    @Column(name = "iva")
    private AfipIva iva;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    @Column(name = "price_per_unit", precision = 10, scale = 2)
    private BigDecimal pricePerUnit;
    @Column(name = "iva_amount", precision = 10, scale = 2)
    private BigDecimal ivaAmount;
    @Column(name = "total", precision = 10, scale = 2)
    private BigDecimal total;

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

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public AfipIva getIva() {
        return iva;
    }

    public void setIva(AfipIva iva) {
        this.iva = iva;
    }

    //    public Long getProductId() {
    //        return product.getId();
    //    }

    //    public void setProductId(Long productId) {
    //        this.product.setId(productId);
    //    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}