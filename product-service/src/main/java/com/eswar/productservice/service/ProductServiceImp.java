package com.eswar.productservice.service;

import com.eswar.productservice.dto.CreateProductRequestDto;
import com.eswar.productservice.dto.ProductResponseDto;
import com.eswar.productservice.dto.UpdateProductRequestDto;

import java.util.List;
import java.util.UUID;

public class ProductServiceImp implements IProductService{
    @Override
    public ProductResponseDto create(CreateProductRequestDto request) {
        return null;
    }

    @Override
    public ProductResponseDto getById(UUID id) {
        return null;
    }

    @Override
    public List<ProductResponseDto> getAll() {
        return List.of();
    }

    @Override
    public ProductResponseDto update(
            UUID id,
            UpdateProductRequestDto request) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }
}
