package com.eswar.inventoryservice.service;

import com.eswar.inventoryservice.dto.InventoryDto;
import com.eswar.inventoryservice.dto.PageResponse;
import com.eswar.inventoryservice.entity.InventoryEntity;
import com.eswar.inventoryservice.exception.InventoryNotFoundException;
import com.eswar.inventoryservice.kafka.event.OrderCreatedEvent;
import com.eswar.inventoryservice.kafka.event.OrderItem;
import com.eswar.inventoryservice.mapper.IInventoryMapper;
import com.eswar.inventoryservice.repository.IInventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImp implements IInventoryService{

    private  final IInventoryRepository inventoryRepository;
    private final IInventoryMapper  inventoryMapper;

    public boolean reserveStock(OrderCreatedEvent event) {

        for (OrderItem item : event.items()) {

            InventoryEntity inventory = inventoryRepository.findById(item.productId()).orElse(null);
            if (inventory == null) {
                log.warn("Product {} not found in inventory", item.productId());
                return false;
            }

            if (inventory.getAvailableQuantity() < item.quantity()) {
                return false;
            }
        }

        // reserve stock
        for (OrderItem item : event.items()) {

            InventoryEntity inventory =
                    inventoryRepository.findById(item.productId()).orElseThrow();

            inventory.setAvailableQuantity(
                    inventory.getAvailableQuantity() - item.quantity()
            );

            inventory.setReservedQuantity(
                    inventory.getReservedQuantity() + item.quantity()
            );

            inventoryRepository.save(inventory);
        }

        return true;
    }

@Transactional
    public InventoryDto createInventory(InventoryDto dto) {

        InventoryEntity entity = inventoryMapper.toEntity(dto);

        InventoryEntity saved = inventoryRepository.save(entity);

        return inventoryMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public InventoryDto getInventory(UUID productId) {

        InventoryEntity entity = inventoryRepository
                .findById(productId)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found"));

        return inventoryMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<InventoryDto> getAllInventories(Pageable pageable) {

        Page<InventoryEntity> page =inventoryRepository.findAll(pageable);

        List<InventoryDto> content=page.getContent().stream().map(inventoryMapper::toDto).toList();

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }



}
