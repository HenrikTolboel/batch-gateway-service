package net.example.batchgateway.application.domain.model.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.example.batchgateway.application.domain.model.usermodule.UserId;

import java.time.Instant;

public class TestEvent extends DomainEvent {
    private final UserId userId;

    public TestEvent(final UserId userId) {
        super();
        this.userId = userId;
    }

    @JsonCreator
    protected TestEvent(final @JsonProperty("domainEventId") DomainEventId domainEventId,
                        final @JsonProperty("timestamp") Instant timestamp,
                        final @JsonProperty("userId") UserId userId) {
        super(domainEventId, timestamp);
        this.userId = userId;
    }

    public UserId getUserId() {
        return userId;
    }
}
