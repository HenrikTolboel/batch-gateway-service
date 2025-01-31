package net.example.batchgateway.application.domain.model.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.example.batchgateway.application.domain.model.BatchId;

import java.time.Instant;

public class BatchCreatedEvent extends DomainEvent {
    private final BatchId batchId;

    public BatchCreatedEvent(final BatchId batchId) {
        super();
        this.batchId = batchId;
    }

    @JsonCreator
    BatchCreatedEvent(final @JsonProperty("domainEventId") DomainEventId domainEventId,
                      final @JsonProperty("timestamp") Instant timestamp,
                      final @JsonProperty("batchId") BatchId batchId) {
        super(domainEventId, timestamp);
        this.batchId = batchId;
    }

    public BatchId getBatchId() {
        return batchId;
    }
}
