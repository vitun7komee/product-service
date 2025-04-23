package com.example.productservice.service;

import com.example.productservice.dto.ProductDto;
import com.example.productservice.entity.Product;
import com.example.productservice.mapper.ProductMapper;
import com.example.productservice.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductDto createProduct(ProductDto dto) {
        log.info("Запрос на создание товара: {}", dto);

        if (!StringUtils.hasText(dto.getName())) {
            log.warn("Ошибка валидации: поле 'name' отсутствует");
            throw new IllegalArgumentException("Поле 'name' обязательно");
        }

        if (dto.getPrice() == null) {
            log.warn("Ошибка валидации: поле 'price' отсутствует");
            throw new IllegalArgumentException("Поле 'price' обязательно");
        }

        Product entity = productMapper.toEntity(dto);
        log.debug("Entity после маппинга: {}", entity);

        Product saved = productRepository.save(entity);
        log.info("Товар успешно создан с ID={}", saved.getId());

        ProductDto result = productMapper.toDto(saved);
        log.debug("Результат DTO: {}", result);
        return result;
    }

    @Override
    public ProductDto getProductById(Long id) {
        log.info("Запрос на получение товара по ID={}", id);

        Product entity = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Товар с ID={} не найден", id);
                    return new EntityNotFoundException("Товар с id=" + id + " не найден");
                });

        ProductDto result = productMapper.toDto(entity);
        log.debug("Полученный товар DTO: {}", result);
        return result;
    }

    @Override
    public List<ProductDto> getAllProducts() {
        log.info("Запрос на получение всех товаров");

        List<ProductDto> result = productRepository.findAll().stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());

        log.debug("Найдено {} товаров", result.size());
        return result;
    }

    @Override
    public ProductDto updateProduct(Long id, ProductDto dto) {
        log.info("Запрос на обновление товара с ID={}, новые данные: {}", id, dto);

        Product existing = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Товар с ID={} не найден для обновления", id);
                    return new EntityNotFoundException("Товар с id=" + id + " не найден");
                });

        if (StringUtils.hasText(dto.getName())) existing.setName(dto.getName());
        if (dto.getDescription() != null) existing.setDescription(dto.getDescription());
        if (dto.getPrice() != null) existing.setPrice(dto.getPrice());
        if (dto.getCategory() != null) existing.setCategory(dto.getCategory());
        if (dto.getStockQuantity() != null) existing.setStockQuantity(dto.getStockQuantity());
        if (dto.getImageUrl() != null) existing.setImageUrl(dto.getImageUrl());
        if (dto.getIsActive() != null) existing.setIsActive(dto.getIsActive());

        Product updated = productRepository.save(existing);

        log.info("Товар с ID={} успешно обновлён", updated.getId());

        ProductDto result = productMapper.toDto(updated);
        log.debug("Обновлённый товар DTO: {}", result);
        return result;
    }

    @Override
    public void deleteProduct(Long id) {
        log.info("Запрос на удаление товара с ID={}", id);

        Product existing = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Товар с ID={} не найден для удаления", id);
                    return new EntityNotFoundException("Товар с id=" + id + " не найден");
                });

        productRepository.delete(existing);
        log.info("Товар с ID={} успешно удалён", id);
    }
}
