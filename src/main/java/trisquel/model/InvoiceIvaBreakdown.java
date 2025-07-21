package trisquel.model;

import trisquel.afip.model.AfipIva;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class InvoiceIvaBreakdown {
    private Map<AfipIva, IvaData> ivaMap;
    private BigDecimal ivaTotal = BigDecimal.ZERO;
    private BigDecimal invoiceNetTotal = BigDecimal.ZERO;
    private BigDecimal invoiceTotal = BigDecimal.ZERO;

    public InvoiceIvaBreakdown(Invoice invoice) {
        this.ivaMap = new HashMap<>();
        for (InvoiceItem item : invoice.getItems()) {
            BigDecimal itemNetAmount = item.getPricePerUnit().multiply(BigDecimal.valueOf(item.getAmount()));
            BigDecimal itemIvaAmount = item.getIvaAmount();
            BigDecimal itemTotal = item.getTotal();

            // Acumular totales
            invoiceNetTotal = invoiceNetTotal.add(itemNetAmount);
            invoiceTotal = invoiceTotal.add(itemTotal);
            ivaTotal = ivaTotal.add(itemIvaAmount);

            // Agrupar por tipo de IVA
            IvaData ivaData = ivaMap.get(item.getIva());
            if (ivaData == null) {
                ivaMap.put(item.getIva(), new IvaData(itemNetAmount, itemIvaAmount));
            } else {
                ivaData.addIva(itemNetAmount, itemIvaAmount);
            }
        }
    }

    public Map<AfipIva, IvaData> getIvaMap() {
        return ivaMap;
    }

    public void setIvaMap(Map<AfipIva, IvaData> ivaMap) {
        this.ivaMap = ivaMap;
    }

    public BigDecimal getIvaTotal() {
        return ivaTotal;
    }

    public void setIvaTotal(BigDecimal ivaTotal) {
        this.ivaTotal = ivaTotal;
    }

    public BigDecimal getInvoiceNetTotal() {
        return invoiceNetTotal;
    }

    public void setInvoiceNetTotal(BigDecimal invoiceNetTotal) {
        this.invoiceNetTotal = invoiceNetTotal;
    }

    public BigDecimal getInvoiceTotal() {
        return invoiceTotal;
    }

    public void setInvoiceTotal(BigDecimal invoiceTotal) {
        this.invoiceTotal = invoiceTotal;
    }

    public static class IvaData {
        private BigDecimal baseImponible;
        private BigDecimal importeIva;

        public IvaData(BigDecimal baseImponible, BigDecimal importeIva) {
            this.baseImponible = baseImponible;
            this.importeIva = importeIva;
        }

        public void addIva(BigDecimal baseToAdd, BigDecimal ivaToAdd) {
            this.baseImponible = this.baseImponible.add(baseToAdd);
            this.importeIva = this.importeIva.add(ivaToAdd);
        }

        // Getters
        public BigDecimal getBaseImponible() {
            return baseImponible;
        }

        public BigDecimal getImporteIva() {
            return importeIva;
        }
    }
}
