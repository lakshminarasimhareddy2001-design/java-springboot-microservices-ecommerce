package com.eswar.productservice.mapper;

import com.eswar.productservice.dto.CategoryRequestDto;
import com.eswar.productservice.dto.CategoryResponseDto;
import com.eswar.productservice.entity.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ICategoryMapper {

    CategoryEntity toEntity(CategoryRequestDto request);

    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    CategoryResponseDto toResponse(CategoryEntity category);
}
