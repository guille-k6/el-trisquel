package com.trisquel.repository;

import com.trisquel.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public
interface ProductRepository extends JpaRepository<Product, Long> {}
