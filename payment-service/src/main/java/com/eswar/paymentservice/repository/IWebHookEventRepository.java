package com.eswar.paymentservice.repository;

import com.eswar.paymentservice.entity.WebhookEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IWebHookEventRepository extends JpaRepository<WebhookEventEntity, UUID> {
}
