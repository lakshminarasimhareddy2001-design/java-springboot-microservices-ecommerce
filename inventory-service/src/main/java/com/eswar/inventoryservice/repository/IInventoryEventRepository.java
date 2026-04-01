package com.eswar.inventoryservice.repository;

import com.eswar.inventoryservice.entity.InventoryEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IInventoryEventRepository extends JpaRepository<InventoryEventEntity, UUID> {
}