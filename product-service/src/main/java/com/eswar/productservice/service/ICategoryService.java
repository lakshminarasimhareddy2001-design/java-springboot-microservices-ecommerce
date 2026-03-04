package com.eswar.productservice.service;

import com.eswar.productservice.dto.CategoryRequestDto;
import com.eswar.productservice.dto.CategoryResponseDto;

import java.util.List;
import java.util.UUID;

public interface ICategoryService {

    CategoryResponseDto create(CategoryRequestDto request);

    CategoryResponseDto getById(UUID id);

    List<CategoryResponseDto> getAll();

    CategoryResponseDto update(UUID id, CategoryRequestDto request);

    void delete(UUID id);
}
