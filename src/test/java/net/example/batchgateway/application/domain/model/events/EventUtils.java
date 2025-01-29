package net.example.batchgateway.application.domain.model.events;

import net.example.batchgateway.config.SpringBootObjectMapper;

final class EventUtils {

    private EventUtils() {}

    static DomainEvent toJsonAndBackToDomainEvent(final DomainEvent event) {
        final String json = SpringBootObjectMapper.writeValueAsString(event);

        final DomainEvent result = SpringBootObjectMapper.readValue(json, DomainEvent.class);

        return result;
    }

}
