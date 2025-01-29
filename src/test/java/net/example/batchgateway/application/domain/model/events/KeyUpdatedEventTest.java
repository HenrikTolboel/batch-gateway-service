package net.example.batchgateway.application.domain.model.events;

import net.example.batchgateway.application.domain.model.keymodule.KeyId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class KeyUpdatedEventTest {

    @Test
    void testSerializeAndDeserialize() {
        final KeyUpdatedEvent event = new KeyUpdatedEvent(KeyId.generate());

        final DomainEvent result = EventUtils.toJsonAndBackToDomainEvent(event);

        assertInstanceOf(KeyUpdatedEvent.class, result);

        assertEquals(event.getKeyId(), ((KeyUpdatedEvent)result).getKeyId());
        assertEquals(event.getDomainEventId(), result.getDomainEventId());
        assertEquals(event.getTimestamp(), result.getTimestamp());
    }

}
