package trisquel.model;

import trisquel.afip.model.AfipIva;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IvaBreakdown {
    private final AfipIva ivaType;
    private final BigDecimal netAmount;
    private final BigDecimal ivaAmount;
    private final BigDecimal totalAmount;
    private final List<InvoiceItemPricing> items;

    public IvaBreakdown(AfipIva ivaType, List<InvoiceItemPricing> items) {
        this.ivaType = ivaType;
        this.items = new ArrayList<>(items);

        // Calculate totals for this IVA group
        this.netAmount = items.stream().map(InvoiceItemPricing::getNetAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        this.ivaAmount = items.stream().map(InvoiceItemPricing::getIvaAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        this.totalAmount = items.stream().map(InvoiceItemPricing::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Getters
    public AfipIva getIvaType() {
        return ivaType;
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

    public List<InvoiceItemPricing> getItems() {
        return Collections.unmodifiableList(items);
    }
}
