package net.example.batchgateway.application.domain.model.events;

import net.example.batchgateway.application.domain.model.usermodule.UserId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class UserCreatedEventTest {

    @Test
    void testSerializeAndDeserialize() {
        final UserCreatedEvent event = new UserCreatedEvent(UserId.generate());

        final DomainEvent result = EventUtils.toJsonAndBackToDomainEvent(event);

        assertInstanceOf(UserCreatedEvent.class, result);

        assertEquals(event.getUserId(), ((UserCreatedEvent)result).getUserId());
        assertEquals(event.getDomainEventId(), result.getDomainEventId());
        assertEquals(event.getTimestamp(), result.getTimestamp());
    }

}
