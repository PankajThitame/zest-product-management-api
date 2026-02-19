package com.zest.product.management.service;

import com.zest.product.management.dto.ProductDto;
import com.zest.product.management.entity.Product;
import com.zest.product.management.exception.ResourceNotFoundException;
import com.zest.product.management.mapper.ProductMapper;
import com.zest.product.management.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

/**
 * Service to handle Product business logic.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public Page<ProductDto> getAllProducts(Pageable pageable) {
        log.info("Fetching products for pageable: {}", pageable);
        return productRepository.findAll(pageable).map(productMapper::toDto);
    }

    @Transactional(readOnly = true)
    public ProductDto getProductById(Long id) {
        log.info("Fetching product with id: {}", id);
        return productRepository.findById(id)
                .map(productMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        log.info("Creating new product with name: {}", productDto.getProductName());
        if (productRepository.existsByProductName(productDto.getProductName())) {
            throw new RuntimeException("Product name already exists");
        }
        Product product = productMapper.toEntity(productDto);
        return productMapper.toDto(productRepository.save(product));
    }

    @Transactional
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        log.info("Updating product with id: {}", id);
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        existingProduct.setProductName(productDto.getProductName());
        return productMapper.toDto(productRepository.save(existingProduct));
    }

    @Transactional
    public void deleteProduct(Long id) {
        log.info("Deleting product with id: {}", id);
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product", "id", id);
        }
        productRepository.deleteById(id);
    }

    @Async
    public CompletableFuture<String> simulateSlowExport() {
        log.info("Starting async product export simulation...");
        try {
            Thread.sleep(5000); // Simulate heavy load
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("Async export completed.");
        return CompletableFuture.completedFuture("Export Finished Successfully at " + System.currentTimeMillis());
    }
}
