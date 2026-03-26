package com.eswar.orderservice.mapper;

import com.eswar.orderservice.dto.OrderDto;
import com.eswar.orderservice.dto.OrderItemDto;
import com.eswar.orderservice.dto.OrderResponseDto;
import com.eswar.orderservice.entity.OrderEntity;
import com.eswar.orderservice.entity.OrderedItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IOrderMapper {
   @Mapping(target = "customerId", ignore = true)
   OrderEntity toEntity(OrderDto orderDto);

   @Mapping(target = "orderId",source = "id")
   OrderResponseDto toResponse(OrderEntity orderEntity);

   @Mapping(target = "productId", source = "id.productId") // 🔥 FIX
   OrderItemDto toItemDto(OrderedItemEntity entity);
}
