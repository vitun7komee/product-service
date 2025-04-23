package com.example.productservice;

import com.example.productservice.controller.ProductController;
import com.example.productservice.dto.ProductDto;
import com.example.productservice.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@org.junit.jupiter.api.extension.ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(productController)
                .build();
    }

    @Test
    void createProduct_ReturnsCreatedDto() throws Exception {
        ProductDto requestDto = ProductDto.builder()
                .name("New Product")
                .description("New Description")
                .price(BigDecimal.valueOf(10.50))
                .category("Category")
                .stockQuantity(5)
                .imageUrl("http://example.com/image.jpg")
                .isActive(true)
                .build();
        ProductDto responseDto = ProductDto.builder()
                .id(1L)
                .name("New Product")
                .description("New Description")
                .price(BigDecimal.valueOf(10.50))
                .category("Category")
                .stockQuantity(5)
                .imageUrl("http://example.com/image.jpg")
                .isActive(true)
                .build();

        given(productService.createProduct(any(ProductDto.class))).willReturn(responseDto);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Product"))
                .andExpect(jsonPath("$.description").value("New Description"))
                .andExpect(jsonPath("$.price").value(10.50))
                .andExpect(jsonPath("$.category").value("Category"))
                .andExpect(jsonPath("$.stockQuantity").value(5))
                .andExpect(jsonPath("$.imageUrl").value("http://example.com/image.jpg"))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void getProduct_ReturnsDto() throws Exception {
        ProductDto dto = ProductDto.builder()
                .id(1L)
                .name("Existing Product")
                .description("Existing Description")
                .price(BigDecimal.valueOf(20.00))
                .category("Category")
                .stockQuantity(10)
                .imageUrl("http://example.com/existing.jpg")
                .isActive(true)
                .build();

        given(productService.getProductById(1L)).willReturn(dto);

        mockMvc.perform(get("/api/products/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Existing Product"))
                .andExpect(jsonPath("$.description").value("Existing Description"))
                .andExpect(jsonPath("$.price").value(20.00))
                .andExpect(jsonPath("$.category").value("Category"))
                .andExpect(jsonPath("$.stockQuantity").value(10))
                .andExpect(jsonPath("$.imageUrl").value("http://example.com/existing.jpg"))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void getAllProducts_ReturnsListOfDtos() throws Exception {
        ProductDto dto1 = ProductDto.builder()
                .id(1L)
                .name("P1")
                .price(BigDecimal.valueOf(5.00))
                .build();
        ProductDto dto2 = ProductDto.builder()
                .id(2L)
                .name("P2")
                .price(BigDecimal.valueOf(15.00))
                .build();

        given(productService.getAllProducts()).willReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("P1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("P2"));
    }

    @Test
    void updateProduct_ReturnsUpdatedDto() throws Exception {
        ProductDto requestDto = ProductDto.builder()
                .name("Updated Product")
                .description("Updated Description")
                .price(BigDecimal.valueOf(30.00))
                .category("UpdatedCategory")
                .stockQuantity(20)
                .imageUrl("http://example.com/updated.jpg")
                .isActive(false)
                .build();
        ProductDto responseDto = ProductDto.builder()
                .id(1L)
                .name("Updated Product")
                .description("Updated Description")
                .price(BigDecimal.valueOf(30.00))
                .category("UpdatedCategory")
                .stockQuantity(20)
                .imageUrl("http://example.com/updated.jpg")
                .isActive(false)
                .build();

        given(productService.updateProduct(eq(1L), any(ProductDto.class))).willReturn(responseDto);

        mockMvc.perform(put("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.price").value(30.00))
                .andExpect(jsonPath("$.category").value("UpdatedCategory"))
                .andExpect(jsonPath("$.stockQuantity").value(20))
                .andExpect(jsonPath("$.imageUrl").value("http://example.com/updated.jpg"))
                .andExpect(jsonPath("$.isActive").value(false));
    }

    @Test
    void deleteProduct_ReturnsNoContent() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}
