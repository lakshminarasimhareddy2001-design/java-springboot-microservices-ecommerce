package com.eswar.productservice.repository;

import com.eswar.productservice.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IProductRepository extends JpaRepository<ProductEntity, UUID> {

    boolean existsBySku(String sku);

    Optional<ProductEntity> findBySku(String sku);
}
