package net.example.batchgateway.application.domain.model.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.example.batchgateway.application.domain.model.applicationmodule.ApplicationId;

import java.time.Instant;

public class ApplicationCreatedEvent extends DomainEvent {
    private final ApplicationId applicationId;

    public ApplicationCreatedEvent(final ApplicationId applicationId) {
        super();
        this.applicationId = applicationId;
    }

    @JsonCreator
    ApplicationCreatedEvent(final @JsonProperty("domainEventId") DomainEventId domainEventId,
                            final @JsonProperty("timestamp") Instant timestamp,
                            final @JsonProperty("applicationId") ApplicationId applicationId) {
        super(domainEventId, timestamp);
        this.applicationId = applicationId;
    }

    public ApplicationId getApplicationId() {
        return applicationId;
    }
}
