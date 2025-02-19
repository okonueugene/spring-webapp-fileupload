package com.store.repository;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import com.store.model.Product;

public interface ProductsRepository extends JpaRepository<Product, Integer> {
       @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE CONCAT('%', LOWER(:keyword), '%') " +
       "OR LOWER(p.brand) LIKE CONCAT('%', LOWER(:keyword), '%') " +
       "OR LOWER(p.category) LIKE CONCAT('%', LOWER(:keyword), '%')")
List<Product> searchProducts(@Param("keyword") String keyword);
}
