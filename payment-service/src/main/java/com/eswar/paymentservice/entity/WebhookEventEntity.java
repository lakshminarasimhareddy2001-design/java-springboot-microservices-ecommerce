package com.eswar.paymentservice.entity;


import com.eswar.paymentservice.audit.AbstractAuditingEntity;
import com.eswar.paymentservice.constatns.WebhookStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "webhook_events",
        indexes = {
                @Index(name = "idx_event_id", columnList = "eventId"),
                @Index(name = "idx_event_type", columnList = "eventType"),
                @Index(name = "idx_status", columnList = "status"),
                @Index(name = "idx_resource_id", columnList = "resourceId")
        })
@Getter
@Setter
public class WebhookEventEntity extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Razorpay event id (IMPORTANT for idempotency)
    @Column(unique = true, nullable = false)
    private String eventId;

    @Column(nullable = false)
    private String eventType;

    // Full payload (store for debugging/replay)
    @Lob
    @Column(nullable = false)
    private String payload;

    private String resourceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WebhookStatus status= WebhookStatus.RECEIVED;

    private String errorMessage;

    private Instant processedAt;

}
