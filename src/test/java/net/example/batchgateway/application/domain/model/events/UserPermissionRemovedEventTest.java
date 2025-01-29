package net.example.batchgateway.application.domain.model.events;

import net.example.batchgateway.application.domain.model.usermodule.PermissionRole;
import net.example.batchgateway.application.domain.model.usermodule.UserId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class UserPermissionRemovedEventTest {

    @Test
    void testSerializeAndDeserialize() {
        final UserPermissionRemovedEvent event = new UserPermissionRemovedEvent(UserId.generate(), new PermissionRole("a role"));

        final DomainEvent result = EventUtils.toJsonAndBackToDomainEvent(event);

        assertInstanceOf(UserPermissionRemovedEvent.class, result);

        assertEquals(event.getUserId(), ((UserPermissionRemovedEvent)result).getUserId());
        assertEquals(event.getPermissionRole(), ((UserPermissionRemovedEvent)result).getPermissionRole());
        assertEquals(event.getDomainEventId(), result.getDomainEventId());
        assertEquals(event.getTimestamp(), result.getTimestamp());
    }

}
