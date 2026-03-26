package com.eswar.inventoryservice.service;

import com.eswar.inventoryservice.dto.InventoryDto;
import com.eswar.inventoryservice.dto.PageResponse;
import com.eswar.inventoryservice.kafka.event.OrderCreatedEvent;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IInventoryService {
    boolean reserveStock(OrderCreatedEvent event);
    InventoryDto createInventory(InventoryDto dto);
    InventoryDto getInventory(UUID productId);

    PageResponse<InventoryDto> getAllInventories(Pageable pageable);

}
