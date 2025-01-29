package net.example.batchgateway.application.domain.model.events;

import net.example.batchgateway.application.domain.model.usermodule.PermissionRole;
import net.example.batchgateway.application.domain.model.usermodule.UserId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class UserPermissionAddedEventTest {

    @Test
    void testSerializeAndDeserialize() {
        final UserPermissionAddedEvent event = new UserPermissionAddedEvent(UserId.generate(), new PermissionRole("a role"));

        final DomainEvent result = EventUtils.toJsonAndBackToDomainEvent(event);

        assertInstanceOf(UserPermissionAddedEvent.class, result);

        assertEquals(event.getUserId(), ((UserPermissionAddedEvent)result).getUserId());
        assertEquals(event.getPermissionRole(), ((UserPermissionAddedEvent)result).getPermissionRole());
        assertEquals(event.getDomainEventId(), result.getDomainEventId());
        assertEquals(event.getTimestamp(), result.getTimestamp());
    }

}
