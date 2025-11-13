package trisquel.model.Dto.Combo;

import trisquel.model.Dto.ProductDTO;

import java.util.List;

public class ProductComboDTO {
    ProductDTO defaultConfig;
    List<ProductDTO> products;

    public ProductComboDTO(ProductDTO defaultConfig, List<ProductDTO> products) {
        this.defaultConfig = defaultConfig;
        this.products = products;
    }

    public ProductComboDTO(List<ProductDTO> products) {
        this.products = products;
    }

    public ProductDTO getDefaultConfig() {
        return defaultConfig;
    }

    public void setDefaultConfig(ProductDTO defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

    public List<ProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDTO> products) {
        this.products = products;
    }
}
