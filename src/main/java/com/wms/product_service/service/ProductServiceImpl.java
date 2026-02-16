package com.wms.product_service.service;

import com.wms.product_service.config.RabbitProductConfig;
import com.wms.product_service.dto.ProductDto;
import com.wms.product_service.entity.Product;
import com.wms.product_service.exception.ResourceNotFoundException;
import com.wms.product_service.mapper.ProductMapper;
import com.wms.product_service.repository.ProductRepository;
import com.wms.product_service.util.Category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    
    private final RabbitTemplate rabbitTemplate;

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductById(Long id) throws ResourceNotFoundException {
        return productRepository.findById(id)
                .map(ProductMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductByCode(String code) throws ResourceNotFoundException {
        return productRepository.findByCode(code)
                .map(ProductMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Product", code));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ProductDto createProduct(ProductDto productDTO) {
        Product product = ProductMapper.toEntity(productDTO);
        Product saved = productRepository.save(product);
        ProductDto resultDto = ProductMapper.toDto(saved);

        // Pubblica l'evento sul Topic Exchange
        // Usiamo una routing key strutturata: product.created o product.updated
        String routingKey = "product.updated"; 
        
        log.info("Invio aggiornamento prodotto su RabbitMQ: {}", resultDto.getCode());
        
        rabbitTemplate.convertAndSend(
            RabbitProductConfig.PRODUCT_EXCHANGE, 
            routingKey, 
            resultDto
        );
        return resultDto;
    }

    @Override
    @Transactional(rollbackFor = ResourceNotFoundException.class, propagation = Propagation.REQUIRED)
    public ProductDto updateProduct(Long id, ProductDto productDTO) throws ResourceNotFoundException {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        existing.setName(productDTO.getName());

        return ProductMapper.toDto(productRepository.save(existing));
    }

    @Override
    @Transactional(rollbackFor = ResourceNotFoundException.class, propagation = Propagation.REQUIRED)
    public void deleteProduct(Long id) throws ResourceNotFoundException {
        if(!productRepository.existsById(id)) throw new ResourceNotFoundException("Product", id);

        productRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> getAllProductsPaged(Pageable pageable) {
        log.debug("Fetching paginated GRNs: page {}, size {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(ProductMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getAllProductByProductCategory(Category category) {
        return productRepository.findByCategory(category)
                .stream()
                .map(ProductMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public void validateProductExists(String productCode) throws ResourceNotFoundException {
        if (!productRepository.existsByCode(productCode)) {
            throw new ResourceNotFoundException("Product", productCode);
        }
    }
}
