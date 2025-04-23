package com.example.productservice.controller;

import com.example.productservice.dto.ProductDto;
import com.example.productservice.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Создать новый товар")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto createProduct(@RequestBody ProductDto dto) {
        log.info("Создание нового продукта: {}", dto);
        ProductDto created = productService.createProduct(dto);
        log.info("Создан продукт с ID: {}", created.getId());
        return created;
    }

    @Operation(summary = "Получить товар по ID")
    @GetMapping("/{id}")
    public ProductDto getProduct(@Parameter(description = "ID товара") @PathVariable Long id) {
        log.info("Получение товара по ID: {}", id);
        ProductDto result = productService.getProductById(id);
        log.info("Получен товар: {}", result);
        return result;
    }

    @Operation(summary = "Получить список всех товаров")
    @GetMapping
    public List<ProductDto> getAllProducts() {
        log.info("Запрос на получение всех товаров");
        List<ProductDto> products = productService.getAllProducts();
        log.info("Получено {} товаров", products.size());
        return products;
    }

    @Operation(summary = "Обновить товар по ID")
    @PutMapping("/{id}")
    public ProductDto updateProduct(@Parameter(description = "ID товара") @PathVariable Long id, @RequestBody ProductDto dto) {
        log.info("Обновление товара ID={} с данными: {}", id, dto);
        ProductDto updated = productService.updateProduct(id, dto);
        log.info("Обновлён товар: {}", updated);
        return updated;
    }

    @Operation(summary = "Удалить товар по ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@Parameter(description = "ID товара") @PathVariable Long id) {
        log.info("Удаление товара с ID: {}", id);
        productService.deleteProduct(id);
        log.info("Товар с ID={} удалён", id);
    }
}
