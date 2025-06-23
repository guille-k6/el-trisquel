package trisquel.afip.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class AfipInvoiceRequest {
    private int conceptType; // 1=Productos, 2=Servicios, 3=Productos y Servicios
    private int documentType; // 80=CUIT, 86=CUIL, 96=DNI, 99=Consumidor Final
    private long documentNumber;
    private long invoiceNumber;
    private LocalDate invoiceDate;
    private BigDecimal totalAmount;
    private BigDecimal netAmount;
    private BigDecimal exemptAmount;
    private BigDecimal taxAmount;
    private String currency = "PES"; // Pesos argentinos
    private BigDecimal exchangeRate = BigDecimal.ONE;
    private List<AfipTax> taxes;
    private List<AfipOptional> optionals;

    public int getConceptType() {
        return conceptType;
    }

    public void setConceptType(int conceptType) {
        this.conceptType = conceptType;
    }

    public int getDocumentType() {
        return documentType;
    }

    public void setDocumentType(int documentType) {
        this.documentType = documentType;
    }

    public long getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(long documentNumber) {
        this.documentNumber = documentNumber;
    }

    public long getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(long invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public BigDecimal getExemptAmount() {
        return exemptAmount;
    }

    public void setExemptAmount(BigDecimal exemptAmount) {
        this.exemptAmount = exemptAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public List<AfipTax> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<AfipTax> taxes) {
        this.taxes = taxes;
    }

    public List<AfipOptional> getOptionals() {
        return optionals;
    }

    public void setOptionals(List<AfipOptional> optionals) {
        this.optionals = optionals;
    }
}
