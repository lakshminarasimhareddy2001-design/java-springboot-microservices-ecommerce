package com.eswar.productservice.service;

import com.eswar.productservice.dto.CreateProductRequestDto;
import com.eswar.productservice.dto.ProductResponseDto;
import com.eswar.productservice.dto.UpdateProductRequestDto;

import java.util.List;
import java.util.UUID;

public interface IProductService {

    ProductResponseDto create(CreateProductRequestDto request);

    ProductResponseDto getById(UUID id);

    List<ProductResponseDto> getAll();

    ProductResponseDto update(UUID id, UpdateProductRequestDto request);

    void delete(UUID id);
}
