package com.eswar.productservice.mapper;

import com.eswar.productservice.dto.CreateProductRequestDto;
import com.eswar.productservice.dto.ProductResponseDto;
import com.eswar.productservice.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IProductMapper {

    ProductEntity toEntity(CreateProductRequestDto request);

    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    ProductResponseDto toResponse(ProductEntity product);

}
