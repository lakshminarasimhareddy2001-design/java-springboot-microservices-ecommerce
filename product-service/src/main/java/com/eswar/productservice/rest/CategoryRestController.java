package com.eswar.productservice.rest;


import com.eswar.productservice.dto.CategoryRequestDto;
import com.eswar.productservice.dto.CategoryResponseDto;
import com.eswar.productservice.service.ICategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Category Service",description = "to create category in ecommerce")
public class CategoryRestController {

    private final ICategoryService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponseDto create(
            @Valid @RequestBody CategoryRequestDto request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getById(@PathVariable UUID id) {
        return  ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity< List<CategoryResponseDto>> getAll() {
        return  ResponseEntity.ok( service.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody CategoryRequestDto request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
