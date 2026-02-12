package com.wms.product_service.mapper;

import com.wms.product_service.dto.ProductDto;
import com.wms.product_service.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public static ProductDto toDto(Product product) {
        if (product == null) {
            return null;
        }
        return ProductDto.builder()
                .id(product.getId())
                .code(product.getCode())
                .name(product.getName())
                .category(product.getCategory())
                .build();
    }

    public static Product toEntity(ProductDto dto) {
        if (dto == null) {
            return null;
        }

        Product product = new Product();
        product.setId(dto.getId());
        product.setCode(dto.getCode());
        product.setName(dto.getName());
        product.setCategory(dto.getCategory());

        return product;
    }
}