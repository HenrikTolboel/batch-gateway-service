package net.example.batchgateway.application.domain.model.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.example.batchgateway.application.domain.model.keymodule.KeyId;

import java.time.Instant;

public class KeyCreatedEvent extends DomainEvent {
    private final KeyId keyId;

    public KeyCreatedEvent(final KeyId keyId) {
        super();
        this.keyId = keyId;
    }

    @JsonCreator
    KeyCreatedEvent(final @JsonProperty("domainEventId") DomainEventId domainEventId,
                    final @JsonProperty("timestamp") Instant timestamp,
                    final @JsonProperty("keyId") KeyId keyId) {
        super(domainEventId, timestamp);
        this.keyId = keyId;
    }

    public KeyId getKeyId() {
        return keyId;
    }
}
