package trisquel.model.Dto;

import trisquel.afip.model.DTO.AfipComprobanteDTO;
import trisquel.afip.model.DTO.AfipConceptoDTO;
import trisquel.afip.model.DTO.AfipMonedaDTO;
import trisquel.afip.model.SellCondition;
import trisquel.model.Invoice;
import trisquel.model.InvoiceQueueStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class InvoiceDTO {
    private Long id;
    private LocalDate date;
    private String comment;
    private Boolean paid;
    private AfipComprobanteDTO comprobante;
    private AfipConceptoDTO concepto;
    private AfipMonedaDTO moneda;
    private InvoiceQueueStatus status;
    private OffsetDateTime createdAt;
    private ClientDTO client;
    private Long numero;
    private BigDecimal total;
    private String cae;
    private LocalDate vtoCae;
    private Long sellPoint;
    private SellCondition sellCondition;
    private List<InvoiceItemDTO> items;

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
        invoiceDTO.setNumero(invoice.getNumero());
        invoiceDTO.setTotal(invoice.getTotal());
        invoiceDTO.setCae(invoice.getCae());
        invoiceDTO.setVtoCae(invoice.getVtoCae());
        invoiceDTO.setSellPoint(invoice.getSellPoint());
        invoiceDTO.setComprobante(AfipComprobanteDTO.fromEnum(invoice.getComprobante()));
        invoiceDTO.setConcepto(AfipConceptoDTO.fromEnum(invoice.getConcepto()));
        invoiceDTO.setMoneda(AfipMonedaDTO.fromEnum(invoice.getMoneda()));
        invoiceDTO.setItems(InvoiceItemDTO.translateToDTOs(invoice.getItems()));
        invoiceDTO.setSellCondition(invoice.getSellCondition());
        return invoiceDTO;
    }

    public static List<InvoiceDTO> translateToDTOs(List<Invoice> invoices) {
        return invoices.stream().map(InvoiceDTO::translateToDTO).collect(Collectors.toList());
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

    public ClientDTO getClient() {
        return client;
    }

    public void setClient(ClientDTO client) {
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

    public AfipComprobanteDTO getComprobante() {
        return comprobante;
    }

    public void setComprobante(AfipComprobanteDTO comprobante) {
        this.comprobante = comprobante;
    }

    public AfipConceptoDTO getConcepto() {
        return concepto;
    }

    public void setConcepto(AfipConceptoDTO concepto) {
        this.concepto = concepto;
    }

    public AfipMonedaDTO getMoneda() {
        return moneda;
    }

    public void setMoneda(AfipMonedaDTO moneda) {
        this.moneda = moneda;
    }

    public List<InvoiceItemDTO> getItems() {
        return items;
    }

    public void setItems(List<InvoiceItemDTO> items) {
        this.items = items;
    }

    public SellCondition getSellCondition() {
        return sellCondition;
    }

    public void setSellCondition(SellCondition sellCondition) {
        this.sellCondition = sellCondition;
    }
}
