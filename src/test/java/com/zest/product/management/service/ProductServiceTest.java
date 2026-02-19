package com.zest.product.management.service;

import com.zest.product.management.dto.ProductDto;
import com.zest.product.management.entity.Product;
import com.zest.product.management.mapper.ProductMapper;
import com.zest.product.management.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        product = Product.builder().id(1L).productName("Test Product").build();
        productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setProductName("Test Product");
    }

    @Test
    void getAllProducts_ShouldReturnPage() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(Collections.singletonList(product));

        when(productRepository.findAll(pageRequest)).thenReturn(productPage);
        when(productMapper.toDto(any(Product.class))).thenReturn(productDto);

        Page<ProductDto> result = productService.getAllProducts(pageRequest);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(productRepository, times(1)).findAll(pageRequest);
    }

    @Test
    void getProductById_WhenFound_ShouldReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(productDto);

        ProductDto result = productService.getProductById(1L);

        assertEquals("Test Product", result.getProductName());
    }

    @Test
    void createProduct_WithUniqueName_ShouldSaveProduct() {
        when(productRepository.existsByProductName(anyString())).thenReturn(false);
        when(productMapper.toEntity(any(ProductDto.class))).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toDto(any(Product.class))).thenReturn(productDto);

        ProductDto result = productService.createProduct(productDto);

        assertNotNull(result);
        assertEquals("Test Product", result.getProductName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void createProduct_WithDuplicateName_ShouldThrowException() {
        when(productRepository.existsByProductName(anyString())).thenReturn(true);
        assertThrows(RuntimeException.class, () -> productService.createProduct(productDto));
    }

    @Test
    void getProductById_WhenNotFound_ShouldThrowException() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> productService.getProductById(99L));
    }

    @Test
    void updateProduct_WhenFound_ShouldUpdate() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toDto(any(Product.class))).thenReturn(productDto);

        ProductDto result = productService.updateProduct(1L, productDto);

        assertNotNull(result);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void deleteProduct_WhenFound_ShouldDelete() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        assertDoesNotThrow(() -> productService.deleteProduct(1L));
        verify(productRepository).deleteById(1L);
    }
}
