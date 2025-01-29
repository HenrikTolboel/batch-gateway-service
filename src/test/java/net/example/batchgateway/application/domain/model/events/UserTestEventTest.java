package net.example.batchgateway.application.domain.model.events;

import net.example.batchgateway.application.domain.model.usermodule.UserId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class UserTestEventTest {

    @Test
    void testSerializeAndDeserialize() {
        final UserTestEvent event = new UserTestEvent(UserId.generate());

        final DomainEvent result = EventUtils.toJsonAndBackToDomainEvent(event);

        assertInstanceOf(UserTestEvent.class, result);

        assertEquals(event.getUserId(), ((UserTestEvent)result).getUserId());
        assertEquals(event.getDomainEventId(), result.getDomainEventId());
        assertEquals(event.getTimestamp(), result.getTimestamp());
    }

}
