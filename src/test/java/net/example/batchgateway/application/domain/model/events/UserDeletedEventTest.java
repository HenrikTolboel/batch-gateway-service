package net.example.batchgateway.application.domain.model.events;

import net.example.batchgateway.application.domain.model.usermodule.UserId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class UserDeletedEventTest {

    @Test
    void testSerializeAndDeserialize() {
        final UserDeletedEvent event = new UserDeletedEvent(UserId.generate());

        final DomainEvent result = EventUtils.toJsonAndBackToDomainEvent(event);

        assertInstanceOf(UserDeletedEvent.class, result);

        assertEquals(event.getUserId(), ((UserDeletedEvent)result).getUserId());
        assertEquals(event.getDomainEventId(), result.getDomainEventId());
        assertEquals(event.getTimestamp(), result.getTimestamp());
    }

}
