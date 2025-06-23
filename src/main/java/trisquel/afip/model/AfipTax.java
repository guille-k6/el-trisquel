package trisquel.afip.model;

import java.math.BigDecimal;

public class AfipTax {
    private int taxId; // 3=IVA, 4=Tributos Nacionales, 5=Tributos Provinciales, 6=Tributos Municipales, 7=Impuestos Internos, 99=Otros
    private BigDecimal baseAmount;
    private BigDecimal aliquot;
    private BigDecimal amount;

    public int getTaxId() {
        return taxId;
    }

    public void setTaxId(int taxId) {
        this.taxId = taxId;
    }

    public BigDecimal getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(BigDecimal baseAmount) {
        this.baseAmount = baseAmount;
    }

    public BigDecimal getAliquot() {
        return aliquot;
    }

    public void setAliquot(BigDecimal aliquot) {
        this.aliquot = aliquot;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
