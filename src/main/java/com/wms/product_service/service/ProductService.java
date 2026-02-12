package com.wms.product_service.service;

import com.wms.product_service.dto.ProductDto;
import com.wms.product_service.exception.ResourceNotFoundException;
import com.wms.product_service.util.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    ProductDto getProductById(Long id) throws ResourceNotFoundException;
    ProductDto getProductByCode(String code) throws ResourceNotFoundException;
    ProductDto createProduct(ProductDto productDTO);
    ProductDto updateProduct(Long id, ProductDto productDTO) throws Exception;
    void deleteProduct(Long id) throws ResourceNotFoundException;
    List<ProductDto> getAllProducts();
    Page<ProductDto> getAllProductsPaged(Pageable pageable);
    List<ProductDto> getAllProductByProductCategory(Category category);

    void validateProductExists(String productCode) throws ResourceNotFoundException;
}
