package com.trisquel.model.Dto;

import com.trisquel.model.DailyBookItem;

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


    public DailyBookItemDTO() {
    }

    public DailyBookItemDTO(Long id, Optional<InvoiceDTO> invoice, Long amount, ProductDTO product, Boolean authorized,
                            ClientDTO client) {
        this.id = id;
        this.invoice = invoice;
        this.amount = amount;
        this.product = product;
        this.authorized = authorized;
        this.client = client;
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
        return dbiDTO;
    }
}
