package trisquel.afip.model;

import java.time.LocalDate;
import java.util.List;

public class AfipInvoiceResponse {
    private String cae;
    private LocalDate caeDueDate;
    private long invoiceNumber;
    private String result; // A=Aprobado, R=Rechazado
    private List<String> errors;
    private List<String> observations;

    public AfipInvoiceResponse(String cae, LocalDate caeDueDate, long invoiceNumber, String result, List<String> errors,
                               List<String> observations) {
        this.cae = cae;
        this.caeDueDate = caeDueDate;
        this.invoiceNumber = invoiceNumber;
        this.result = result;
        this.errors = errors;
        this.observations = observations;
    }

    public AfipInvoiceResponse(String cae, LocalDate caeDueDate, String result) {
        this.cae = cae;
        this.caeDueDate = caeDueDate;
        this.result = result;
    }

    public String getCae() {
        return cae;
    }

    public void setCae(String cae) {
        this.cae = cae;
    }

    public LocalDate getCaeDueDate() {
        return caeDueDate;
    }

    public void setCaeDueDate(LocalDate caeDueDate) {
        this.caeDueDate = caeDueDate;
    }

    public long getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(long invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getObservations() {
        return observations;
    }

    public void setObservations(List<String> observations) {
        this.observations = observations;
    }
}
