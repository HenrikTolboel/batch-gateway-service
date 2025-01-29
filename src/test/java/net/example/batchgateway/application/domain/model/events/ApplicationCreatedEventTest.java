package net.example.batchgateway.application.domain.model.events;

import net.example.batchgateway.application.domain.model.applicationmodule.ApplicationId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationCreatedEventTest {

    @Test
    void testSerializeAndDeserialize() {
        final ApplicationCreatedEvent event = new ApplicationCreatedEvent(ApplicationId.generate());

        final DomainEvent result = EventUtils.toJsonAndBackToDomainEvent(event);

        assertInstanceOf(ApplicationCreatedEvent.class, result);

        assertEquals(event.getApplicationId(), ((ApplicationCreatedEvent)result).getApplicationId());
        assertEquals(event.getDomainEventId(), result.getDomainEventId());
        assertEquals(event.getTimestamp(), result.getTimestamp());
    }

}
