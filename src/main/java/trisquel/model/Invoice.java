package trisquel.model;

import jakarta.persistence.*;
import trisquel.afip.model.AfipComprobante;
import trisquel.afip.model.AfipConcepto;
import trisquel.afip.model.AfipMoneda;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoice")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoice_seq")
    @SequenceGenerator(name = "invoice_seq", sequenceName = "invoice_seq", allocationSize = 1)
    private Long id;
    private LocalDate date;
    private String comment;
    private Boolean paid;
    private AfipComprobante comprobante;
    private AfipConcepto concepto;
    private AfipMoneda moneda;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InvoiceQueueStatus status = InvoiceQueueStatus.QUEUED;
    @Column(name = "created_at")
    private OffsetDateTime createdAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    private Long numero;
    private BigDecimal total;
    private String cae;
    private LocalDate vtoCae;
    private Long sellPoint = 2L;
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<InvoiceItem> items = new ArrayList<>();

    public Invoice() {
    }

    public Invoice(Long id) {
        this.id = id;
    }

    // Getters y Setters

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

    public InvoiceQueueStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceQueueStatus status) {
        this.status = status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
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

    public AfipComprobante getComprobante() {
        return comprobante;
    }

    public void setComprobante(AfipComprobante comprobante) {
        this.comprobante = comprobante;
    }

    public AfipConcepto getConcepto() {
        return concepto;
    }

    public void setConcepto(AfipConcepto concepto) {
        this.concepto = concepto;
    }

    public List<InvoiceItem> getItems() {
        return items;
    }

    public void setItems(List<InvoiceItem> items) {
        this.items = items;
    }

    public AfipMoneda getMoneda() {
        return moneda;
    }

    public void setMoneda(AfipMoneda moneda) {
        this.moneda = moneda;
    }
}