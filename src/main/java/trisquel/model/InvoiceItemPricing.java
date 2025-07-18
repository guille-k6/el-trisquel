package trisquel.model;

import trisquel.afip.model.AfipIva;

import java.math.BigDecimal;

public class InvoiceItemPricing {
    private final Long itemId;
    private final Integer quantity;
    private final BigDecimal pricePerUnit;
    private final AfipIva iva;
    private final BigDecimal netAmount;
    private final BigDecimal ivaAmount;
    private final BigDecimal totalAmount;

    public InvoiceItemPricing(InvoiceItem item) {
        this.itemId = item.getId();
        this.quantity = item.getAmount();
        this.pricePerUnit = BigDecimal.valueOf(item.getPricePerUnit());
        this.iva = item.getIva();

        // Calculate net amount (quantity * price per unit)
        this.netAmount = this.pricePerUnit.multiply(BigDecimal.valueOf(this.quantity));

        // Calculate IVA amount
        BigDecimal ivaPercentage = BigDecimal.valueOf(this.iva.getPercentage()).divide(BigDecimal.valueOf(100));
        this.ivaAmount = this.netAmount.multiply(ivaPercentage);

        // Calculate total amount (net + IVA)
        this.totalAmount = this.netAmount.add(this.ivaAmount);
    }

    // Getters
    public Long getItemId() {
        return itemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public AfipIva getIva() {
        return iva;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public BigDecimal getIvaAmount() {
        return ivaAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}
