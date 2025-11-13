package trisquel.model.Dto;

import trisquel.model.Product;

import java.util.List;
import java.util.stream.Collectors;

public class ProductDTO {
    private Long id;
    private String name;
    private String measureUnit;

    public ProductDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public ProductDTO() {
    }

    public ProductDTO(Long id) {
        this.id = id;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMeasureUnit() {
        return measureUnit;
    }

    public void setMeasureUnit(String measureUnit) {
        this.measureUnit = measureUnit;
    }

    public static ProductDTO translateToDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setMeasureUnit(product.getMeasureUnit());
        return productDTO;
    }

    public static List<ProductDTO> translateToDTO(List<Product> products) {
        return products.stream().map(ProductDTO::translateToDTO).collect(Collectors.toList());
    }
}
