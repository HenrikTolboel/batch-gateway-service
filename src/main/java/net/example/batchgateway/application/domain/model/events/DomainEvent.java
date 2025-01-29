package net.example.batchgateway.application.domain.model.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.Instant;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "eventType")
public abstract class DomainEvent  {
    private final Instant timestamp;

    private final DomainEventId domainEventId;

    @JsonCreator
    protected DomainEvent(final @JsonProperty("domainEventId") DomainEventId domainEventId,
                          final @JsonProperty("timestamp") Instant timestamp) {
        this.domainEventId = domainEventId;
        this.timestamp = timestamp;
    }

    public DomainEvent() {
        this.domainEventId = DomainEventId.generate();
        this.timestamp = Instant.now();
    }

    public final DomainEventId getDomainEventId() {
        return domainEventId;
    }

    public final Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[id=" + domainEventId + ", timestamp=" + timestamp + "]";
    }

}
