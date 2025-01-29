package net.example.batchgateway.application.domain.model.events;

import net.example.batchgateway.application.domain.model.keymodule.KeyId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class KeyCreatedEventTest {

    @Test
    void testSerializeAndDeserialize() {
        final KeyCreatedEvent event = new KeyCreatedEvent(KeyId.generate());

        final DomainEvent result = EventUtils.toJsonAndBackToDomainEvent(event);

        assertInstanceOf(KeyCreatedEvent.class, result);

        assertEquals(event.getKeyId(), ((KeyCreatedEvent)result).getKeyId());
        assertEquals(event.getDomainEventId(), result.getDomainEventId());
        assertEquals(event.getTimestamp(), result.getTimestamp());
    }

}
