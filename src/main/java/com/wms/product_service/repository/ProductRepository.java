package com.wms.product_service.repository;

import com.wms.product_service.entity.Product;
import com.wms.product_service.util.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByCode(String code);
    List<Product> findByCategory(Category category);
    boolean existsByCode(String code);
}
