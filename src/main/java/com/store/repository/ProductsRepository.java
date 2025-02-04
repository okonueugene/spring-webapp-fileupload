package com.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.store.model.Product;

public interface ProductsRepository extends JpaRepository<Product, Integer> {

}
