package net.example.batchgateway.application.domain.model.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.example.batchgateway.application.domain.model.BatchId;

import java.time.Instant;

public class BatchUpdatedEvent extends DomainEvent {
    private final BatchId batchId;

    public BatchUpdatedEvent(final BatchId batchId) {
        super();
        this.batchId = batchId;
    }

    @JsonCreator
    BatchUpdatedEvent(final @JsonProperty("domainEventId") DomainEventId domainEventId,
                      final @JsonProperty("timestamp") Instant timestamp,
                      final @JsonProperty("batchId") BatchId batchId) {
        super(domainEventId, timestamp);
        this.batchId = batchId;
    }

    public BatchId getBatchId() {
        return batchId;
    }
}
