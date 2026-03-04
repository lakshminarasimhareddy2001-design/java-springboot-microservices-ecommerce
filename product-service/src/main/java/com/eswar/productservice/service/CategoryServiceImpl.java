package com.eswar.productservice.service;

import com.eswar.productservice.dto.*;
import com.eswar.productservice.entity.CategoryEntity;
import com.eswar.productservice.exception.*;
import com.eswar.productservice.mapper.ICategoryMapper;
import com.eswar.productservice.repository.ICategoryRepository;
import com.eswar.productservice.service.ICategoryService;
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
public class CategoryServiceImpl implements ICategoryService {

    private final ICategoryRepository repository;
    private final ICategoryMapper mapper;

    @Override
    public CategoryResponseDto create(CategoryRequestDto request) {

        log.info("Creating category: {}", request.name());

        if (repository.existsByName(request.name())) {
            throw new DuplicateResourceException("Category already exists");
        }

        CategoryEntity category = mapper.toEntity(request);
        CategoryEntity saved = repository.save(category);

        return mapper.toResponse(saved);
    }

    @Override
    public CategoryResponseDto getById(UUID id) {

        CategoryEntity category = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        return mapper.toResponse(category);
    }

    @Override
    public List<CategoryResponseDto> getAll() {

        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public CategoryResponseDto update(UUID id, CategoryRequestDto request) {

        CategoryEntity category = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        category.setName(request.name());
        category.setDescription(request.description());

        return mapper.toResponse(repository.save(category));
    }

    @Override
    public void delete(UUID id) {

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found");
        }

        repository.deleteById(id);
    }
}