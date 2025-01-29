package net.example.batchgateway.application.domain.model.events;

import net.example.batchgateway.application.domain.model.applicationmodule.ApplicationId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ApplicationDeletedEventTest {

    @Test
    void testSerializeAndDeserialize() {
        final ApplicationDeletedEvent event = new ApplicationDeletedEvent(ApplicationId.generate());

        final DomainEvent result = EventUtils.toJsonAndBackToDomainEvent(event);

        assertInstanceOf(ApplicationDeletedEvent.class, result);

        assertEquals(event.getApplicationId(), ((ApplicationDeletedEvent)result).getApplicationId());
        assertEquals(event.getDomainEventId(), result.getDomainEventId());
        assertEquals(event.getTimestamp(), result.getTimestamp());
    }

}
