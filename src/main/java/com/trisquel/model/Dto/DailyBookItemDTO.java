package com.trisquel.model.Dto;

import com.trisquel.model.Client;
import com.trisquel.model.DailyBookItem;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DailyBookItemDTO {
    private Long id;
    private Optional<InvoiceDTO> invoice;
    private Long amount;
    private ProductDTO product;
    private Boolean authorized;
    private ClientDTO client;
    private Client voucher;
    private LocalDate date;
    private Long payment;

    private String observations;


    public DailyBookItemDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Optional<InvoiceDTO> getInvoice() {
        return invoice;
    }

    public void setInvoice(Optional<InvoiceDTO> invoice) {
        this.invoice = invoice;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    public Boolean getAuthorized() {
        return authorized;
    }

    public void setAuthorized(Boolean authorized) {
        this.authorized = authorized;
    }

    public ClientDTO getClient() {
        return client;
    }

    public void setClient(ClientDTO client) {
        this.client = client;
    }

    public Client getVoucher() {
        return voucher;
    }

    public void setVoucher(Client voucher) {
        this.voucher = voucher;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getPayment() {
        return payment;
    }

    public void setPayment(Long payment) {
        this.payment = payment;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public static List<DailyBookItemDTO> translateToDTOs(List<DailyBookItem> dailyBookItems) {
        return dailyBookItems.stream().map(DailyBookItemDTO::translateToDTO).collect(Collectors.toList());
    }

    public static DailyBookItemDTO translateToDTO(DailyBookItem dailyBookItem) {
        DailyBookItemDTO dbiDTO = new DailyBookItemDTO();
        dbiDTO.setId(dailyBookItem.getId());
        dbiDTO.setInvoice(InvoiceDTO.translateToDTO(dailyBookItem.getInvoice()));
        dbiDTO.setAmount(dailyBookItem.getAmount());
        dbiDTO.setProduct(ProductDTO.translateToDTO(dailyBookItem.getProduct()));
        dbiDTO.setAuthorized(dailyBookItem.getAuthorized());
        dbiDTO.setClient(ClientDTO.translateToDTO(dailyBookItem.getClient()));
        dbiDTO.setVoucher(dailyBookItem.getVoucher());
        dbiDTO.setDate(dailyBookItem.getDate());
        dbiDTO.setPayment(dailyBookItem.getPayment());
        return dbiDTO;
    }
}
