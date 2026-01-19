package com.example.S2S.service;

import com.example.S2S.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    Product createProduct(Product product);

    Product submitProduct(String productId);

    Product approveProduct(String productId);

    Product confirmBuyer(String productId, String buyerId);

    Page<Product> getApprovedProducts(Pageable pageable);
}

