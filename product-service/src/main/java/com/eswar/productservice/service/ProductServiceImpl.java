package com.eswar.productservice.service;

import com.eswar.productservice.constatnts.ProductStatus;
import com.eswar.productservice.dto.*;
import com.eswar.productservice.entity.*;
import com.eswar.productservice.exception.*;
import com.eswar.productservice.mapper.IProductMapper;
import com.eswar.productservice.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.DuplicateResourceException;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements IProductService {

    private final IProductRepository productRepository;
    private final ICategoryRepository categoryRepository;
    private final IProductMapper mapper;

    @Override
    public ProductResponseDto create(CreateProductRequestDto request) {

        log.info("Creating product with SKU: {}", request.sku());

        if (productRepository.existsBySku(request.sku())) {
            throw new DuplicateResourceException("Product with SKU already exists");
        }

        CategoryEntity category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        ProductEntity product = mapper.toEntity(request);
        product.setCategory(category);
        product.setStatus(ProductStatus.ACTIVE);

        ProductEntity saved = productRepository.save(product);

        return mapper.toResponse(saved);
    }

    @Override
    public ProductResponseDto getById(UUID id) {

        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        return mapper.toResponse(product);
    }

    @Override
    public List<ProductResponseDto> getAll() {

        return productRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public ProductResponseDto update(UUID id, UpdateProductRequestDto request) {

        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());

        return mapper.toResponse(productRepository.save(product));
    }

    @Override
    public void delete(UUID id) {

        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found");
        }

        productRepository.deleteById(id);
    }
}