package com.familyvault.core.domain.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Base interface for all domain events.
 * Events are immutable records of something that happened.
 */
public interface DomainEvent {

    /**
     * Unique identifier for this event instance.
     */
    UUID getEventId();

    /**
     * Type name for serialization/routing.
     */
    String getEventType();

    /**
     * When the event occurred.
     */
    Instant getOccurredAt();

    /**
     * ID of the aggregate this event belongs to.
     */
    UUID getAggregateId();

    /**
     * Type of aggregate (e.g., "User", "Family", "File").
     */
    String getAggregateType();
}
