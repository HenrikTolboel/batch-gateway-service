package net.example.batchgateway.application.domain.model.events;

import net.example.batchgateway.application.domain.model.tenantmodule.TenantId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class TenantCreatedEventTest {

    @Test
    void testSerializeAndDeserialize() {
        final TenantCreatedEvent event = new TenantCreatedEvent(TenantId.generate());

        final DomainEvent result = EventUtils.toJsonAndBackToDomainEvent(event);

        assertInstanceOf(TenantCreatedEvent.class, result);

        assertEquals(event.getTenantId(), ((TenantCreatedEvent)result).getTenantId());
        assertEquals(event.getDomainEventId(), result.getDomainEventId());
        assertEquals(event.getTimestamp(), result.getTimestamp());
    }

}
