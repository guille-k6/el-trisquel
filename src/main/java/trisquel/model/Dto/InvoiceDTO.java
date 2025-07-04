package trisquel.model.Dto;

import trisquel.model.Invoice;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class InvoiceDTO {
    private Long id;
    private LocalDate date;
    private String comment;
    private Boolean paid;
    private String status;
    private OffsetDateTime createdAt;
    private ClientDTO client;
    private String tipo;
    private Long numero;
    private Double total;
    private String cae;
    private LocalDate vtoCae;
    private Long sellPoint;

    public InvoiceDTO() {
    }

    public static InvoiceDTO translateToDTO(Invoice invoice) {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice cannot be null");
        }
        InvoiceDTO invoiceDTO = new InvoiceDTO();
        invoiceDTO.setId(invoice.getId());
        invoiceDTO.setDate(invoice.getDate());
        invoiceDTO.setComment(invoice.getComment());
        invoiceDTO.setPaid(invoice.getPaid());
        invoiceDTO.setStatus(invoice.getStatus());
        invoiceDTO.setCreatedAt(invoice.getCreatedAt());
        invoiceDTO.setClient(ClientDTO.translateToDTO(invoice.getClient()));
        invoiceDTO.setTipo(invoice.getTipo());
        invoiceDTO.setNumero(invoice.getNumero());
        invoiceDTO.setTotal(invoice.getTotal());
        invoiceDTO.setCae(invoice.getCae());
        invoiceDTO.setVtoCae(invoice.getVtoCae());
        invoiceDTO.setSellPoint(invoice.getSellPoint());
        return invoiceDTO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ClientDTO getClient() {
        return client;
    }

    public void setClient(ClientDTO client) {
        this.client = client;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getCae() {
        return cae;
    }

    public void setCae(String cae) {
        this.cae = cae;
    }

    public LocalDate getVtoCae() {
        return vtoCae;
    }

    public void setVtoCae(LocalDate vtoCae) {
        this.vtoCae = vtoCae;
    }

    public Long getSellPoint() {
        return sellPoint;
    }

    public void setSellPoint(Long sellPoint) {
        this.sellPoint = sellPoint;
    }
}
