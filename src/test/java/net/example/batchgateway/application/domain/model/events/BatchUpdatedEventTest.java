package net.example.batchgateway.application.domain.model.events;

import net.example.batchgateway.application.domain.model.BatchId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class BatchUpdatedEventTest {

    @Test
    void testSerializeAndDeserialize() {
        final BatchUpdatedEvent event = new BatchUpdatedEvent(BatchId.generate());

        final DomainEvent result = EventUtils.toJsonAndBackToDomainEvent(event);

        assertInstanceOf(BatchUpdatedEvent.class, result);

        assertEquals(event.getBatchId(), ((BatchUpdatedEvent)result).getBatchId());
        assertEquals(event.getDomainEventId(), result.getDomainEventId());
        assertEquals(event.getTimestamp(), result.getTimestamp());
    }

}
