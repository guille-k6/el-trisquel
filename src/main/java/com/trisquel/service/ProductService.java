package com.trisquel.service;

import com.trisquel.model.Product;
import com.trisquel.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
class ProductService {
    @Autowired
    public ProductService(ProductRepository repository){
        this.repository = repository;
    }
    private final ProductRepository repository;
    public List<Product> findAll() { return repository.findAll(); }
    public Optional<Product> findById(Long id) { return repository.findById(id); }
    public Product save(Product product) { return repository.save(product); }
    public void delete(Long id) { repository.deleteById(id); }
}
