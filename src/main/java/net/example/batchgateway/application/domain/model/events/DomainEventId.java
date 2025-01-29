package net.example.batchgateway.application.domain.model.events;

import java.util.UUID;

public record DomainEventId(UUID value) {
    public static DomainEventId generate() {
        return new DomainEventId(UUID.randomUUID());
    }

}

