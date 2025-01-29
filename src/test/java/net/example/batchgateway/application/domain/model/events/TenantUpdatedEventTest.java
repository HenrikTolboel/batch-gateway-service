package net.example.batchgateway.application.domain.model.events;

import net.example.batchgateway.application.domain.model.tenantmodule.TenantId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class TenantUpdatedEventTest {

    @Test
    void testSerializeAndDeserialize() {
        final TenantUpdatedEvent event = new TenantUpdatedEvent(TenantId.generate());

        final DomainEvent result = EventUtils.toJsonAndBackToDomainEvent(event);

        assertInstanceOf(TenantUpdatedEvent.class, result);

        assertEquals(event.getTenantId(), ((TenantUpdatedEvent)result).getTenantId());
        assertEquals(event.getDomainEventId(), result.getDomainEventId());
        assertEquals(event.getTimestamp(), result.getTimestamp());
    }

}
