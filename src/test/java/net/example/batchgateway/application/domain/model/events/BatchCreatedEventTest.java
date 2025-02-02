package net.example.batchgateway.application.domain.model.events;

import net.example.batchgateway.application.domain.model.BatchId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BatchCreatedEventTest {

    @Test
    void testSerializeAndDeserialize() {
        final BatchCreatedEvent event = new BatchCreatedEvent(BatchId.generate());

        final DomainEvent result = EventUtils.toJsonAndBackToDomainEvent(event);

        assertInstanceOf(BatchCreatedEvent.class, result);

        assertEquals(event.getBatchId(), ((BatchCreatedEvent)result).getBatchId());
        assertEquals(event.getDomainEventId(), result.getDomainEventId());
        assertEquals(event.getTimestamp(), result.getTimestamp());
    }

}
