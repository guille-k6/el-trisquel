package trisquel.model.Dto;

import trisquel.model.Invoice;
import trisquel.model.InvoiceItem;
import trisquel.model.Product;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PricesSuggestionDTO {
    ProductDTO product;
    List<PriceForClient> productLastPricesByClient = new ArrayList<>();
    List<PriceForClient> productLastPrices = new ArrayList<>();

    public static PricesSuggestionDTO getPricesSuggestion(Product product, List<Invoice> lastPricesForClient,
                                                          List<Invoice> lastPricesForProduct) {
        PricesSuggestionDTO priceSuggestion = new PricesSuggestionDTO();
        List<PriceForClient> priceForClients = new ArrayList<>();
        for (Invoice invoice : lastPricesForClient) {
            InvoiceItem productItem = invoice.getItems().stream().filter(i -> i.getProduct().getId().equals(product.getId())).findFirst().get();
            priceForClients.add(new PriceForClient(invoice.getClient().getName(), invoice.getDate(), productItem.getPricePerUnit(), productItem.getAmount()));
        }
        List<PriceForClient> generalPrices = new ArrayList<>();
        for (Invoice invoice : lastPricesForProduct) {
            InvoiceItem productItem = invoice.getItems().stream().filter(i -> i.getProduct().getId().equals(product.getId())).findFirst().get();
            generalPrices.add(new PriceForClient(invoice.getClient().getName(), invoice.getDate(), productItem.getPricePerUnit(), productItem.getAmount()));
        }
        // Fill DTO
        priceSuggestion.product = ProductDTO.translateToDTO(product);
        priceSuggestion.productLastPricesByClient = priceForClients;
        priceSuggestion.productLastPrices = generalPrices;
        return priceSuggestion;
    }

    public PricesSuggestionDTO() {
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    public List<PriceForClient> getProductLastPrices() {
        return productLastPrices;
    }

    public void setProductLastPrices(List<PriceForClient> productLastPrices) {
        this.productLastPrices = productLastPrices;
    }

    public List<PriceForClient> getProductLastPricesByClient() {
        return productLastPricesByClient;
    }

    public void setProductLastPricesByClient(List<PriceForClient> productLastPricesByClient) {
        this.productLastPricesByClient = productLastPricesByClient;
    }

    public record PriceForClient(String clientName, LocalDate invoiceDate, BigDecimal price, Integer amount) {
    }
}
