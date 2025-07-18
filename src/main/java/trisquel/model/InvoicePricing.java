package trisquel.model;

import trisquel.afip.model.AfipIva;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InvoicePricing {
    private final Long invoiceId;
    private final List<InvoiceItemPricing> itemPricings;
    private final List<IvaBreakdown> ivaBreakdowns;
    private final BigDecimal netAmount;
    private final BigDecimal totalIvaAmount;
    private final BigDecimal total;

    public InvoicePricing(Invoice invoice) {
        this.invoiceId = invoice.getId();

        // Calculate pricing for each item
        this.itemPricings = invoice.getItems().stream().map(InvoiceItemPricing::new).collect(Collectors.toList());

        // Group by IVA type and create breakdowns
        Map<AfipIva, List<InvoiceItemPricing>> groupedByIva = itemPricings.stream().collect(Collectors.groupingBy(InvoiceItemPricing::getIva));

        this.ivaBreakdowns = groupedByIva.entrySet().stream().map(entry -> new IvaBreakdown(entry.getKey(), entry.getValue())).collect(Collectors.toList());

        // Calculate invoice totals
        this.netAmount = itemPricings.stream().map(InvoiceItemPricing::getNetAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        this.totalIvaAmount = itemPricings.stream().map(InvoiceItemPricing::getIvaAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        this.total = this.netAmount.add(this.totalIvaAmount);
    }

    // Getters
    public Long getInvoiceId() {
        return invoiceId;
    }

    public List<InvoiceItemPricing> getItemPricings() {
        return Collections.unmodifiableList(itemPricings);
    }

    public List<IvaBreakdown> getIvaBreakdowns() {
        return Collections.unmodifiableList(ivaBreakdowns);
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public BigDecimal getTotalIvaAmount() {
        return totalIvaAmount;
    }

    public BigDecimal getTotal() {
        return total;
    }

    // Utility methods
    public BigDecimal getIvaAmountByType(AfipIva ivaType) {
        return ivaBreakdowns.stream().filter(breakdown -> breakdown.getIvaType() == ivaType).map(IvaBreakdown::getIvaAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getNetAmountByIvaType(AfipIva ivaType) {
        return ivaBreakdowns.stream().filter(breakdown -> breakdown.getIvaType() == ivaType).map(IvaBreakdown::getNetAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
