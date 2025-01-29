package net.example.batchgateway.application.events;

import net.example.batchgateway.application.domain.model.events.DomainEvent;

public class IntegrationEvent {

    private final DomainEvent domainEvent;

    public IntegrationEvent(final DomainEvent domainEvent) {
        this.domainEvent = domainEvent;
    }




}
