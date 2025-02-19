package com.store.service;
import java.util.List;

import org.springframework.beans.factory.annotation.*;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.store.model.Product;
import com.store.repository.ProductsRepository;
@Service
public class ProductsService {
    @Autowired
    private ProductsRepository productsRepository;

    public List<Product> searchProducts(@RequestParam String keyword) {
        return productsRepository.searchProducts(keyword);
    }
}
