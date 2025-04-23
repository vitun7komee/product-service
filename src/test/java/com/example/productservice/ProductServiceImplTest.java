package com.example.productservice;

import com.example.productservice.dto.ProductDto;
import com.example.productservice.entity.Product;
import com.example.productservice.mapper.ProductMapper;
import com.example.productservice.repository.ProductRepository;
import com.example.productservice.service.ProductServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        product = Product.builder()
                .id(1L)
                .name("Product 1")
                .price(BigDecimal.valueOf(100.0))
                .description("Description")
                .category("Category")
                .stockQuantity(10)
                .imageUrl("http://example.com/image.jpg")
                .isActive(true)
                .build();

        productDto = ProductDto.builder()
                .id(1L)
                .name("Product 1")
                .price(BigDecimal.valueOf(100.0))
                .description("Description")
                .category("Category")
                .stockQuantity(10)
                .imageUrl("http://example.com/image.jpg")
                .isActive(true)
                .build();
    }

    @Test
    void testCreateProduct_Success() {
        when(productMapper.toEntity(productDto)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(productDto);
        ProductDto result = productService.createProduct(productDto);
        assertNotNull(result);
        assertEquals("Product 1", result.getName());
        assertEquals(BigDecimal.valueOf(100.0), result.getPrice());
    }

    @Test
    void testCreateProduct_ValidationFailure() {
        ProductDto invalidDto = ProductDto.builder()
                .name("")
                .price(BigDecimal.valueOf(100.0))
                .build();

        assertThrows(IllegalArgumentException.class, () -> productService.createProduct(invalidDto));
    }

    @Test
    void testGetProductById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(productDto);
        ProductDto result = productService.getProductById(1L);
        assertNotNull(result);
        assertEquals("Product 1", result.getName());
    }

    @Test
    void testGetProductById_NotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.getProductById(1L));
    }

    @Test
    void testUpdateProduct_Success() {

        ProductDto updatedDto = ProductDto.builder()
                .id(1L)
                .name("Updated Product")
                .price(BigDecimal.valueOf(150.0))
                .description("Updated Description")
                .category("Updated Category")
                .stockQuantity(20)
                .imageUrl("http://example.com/updated_image.jpg")
                .isActive(true)
                .build();

        Product updatedProduct = Product.builder()
                .id(1L)
                .name("Updated Product")
                .price(BigDecimal.valueOf(150.0))
                .description("Updated Description")
                .category("Updated Category")
                .stockQuantity(20)
                .imageUrl("http://example.com/updated_image.jpg")
                .isActive(true)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(updatedProduct);
        when(productMapper.toDto(updatedProduct)).thenReturn(updatedDto);
        ProductDto result = productService.updateProduct(1L, updatedDto);

        assertNotNull(result);
        assertEquals("Updated Product", result.getName());
        assertEquals(BigDecimal.valueOf(150.0), result.getPrice());
        assertEquals("Updated Description", result.getDescription());
        assertEquals("Updated Category", result.getCategory());
        assertEquals(20, result.getStockQuantity());
        assertEquals("http://example.com/updated_image.jpg", result.getImageUrl());
        assertTrue(result.getIsActive());

        verify(productRepository).save(product);
    }

    @Test
    void testUpdateProduct_NotFound() {
        ProductDto updatedDto = ProductDto.builder()
                .id(1L)
                .name("Updated Product")
                .price(BigDecimal.valueOf(150.0))
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.updateProduct(1L, updatedDto));
    }

    @Test
    void testDeleteProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void testDeleteProduct_NotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.deleteProduct(1L));
    }
}
