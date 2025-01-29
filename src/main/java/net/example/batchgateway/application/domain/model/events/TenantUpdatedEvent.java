package net.example.batchgateway.application.domain.model.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.example.batchgateway.application.domain.model.tenantmodule.TenantId;

import java.time.Instant;

public class TenantUpdatedEvent extends DomainEvent {
    private final TenantId tenantId;

    public TenantUpdatedEvent(final TenantId tenantId) {
        super();
        this.tenantId = tenantId;
    }

    @JsonCreator
    TenantUpdatedEvent(final @JsonProperty("domainEventId") DomainEventId domainEventId,
                       final @JsonProperty("timestamp") Instant timestamp,
                       final @JsonProperty("tenantId") TenantId tenantId) {
        super(domainEventId, timestamp);
        this.tenantId = tenantId;
    }

    public TenantId getTenantId() {
        return tenantId;
    }

}
