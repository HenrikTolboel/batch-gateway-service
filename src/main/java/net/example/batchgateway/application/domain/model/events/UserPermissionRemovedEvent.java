package net.example.batchgateway.application.domain.model.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.example.batchgateway.application.domain.model.usermodule.PermissionRole;
import net.example.batchgateway.application.domain.model.usermodule.UserId;

import java.time.Instant;

public class UserPermissionRemovedEvent extends DomainEvent {
    private final UserId userId;
    private final PermissionRole permissionRole;

    public UserPermissionRemovedEvent(final UserId userId, final PermissionRole permissionRole) {
        super();
        this.userId = userId;
        this.permissionRole = permissionRole;
    }

    @JsonCreator
    UserPermissionRemovedEvent(final @JsonProperty("domainEventId") DomainEventId domainEventId,
                               final @JsonProperty("timestamp") Instant timestamp,
                               final @JsonProperty("userId") UserId userId,
                               final @JsonProperty("permissionRole") PermissionRole permissionRole) {
        super(domainEventId, timestamp);
        this.userId = userId;
        this.permissionRole = permissionRole;
    }

    public UserId getUserId() {
        return userId;
    }

    public PermissionRole getPermissionRole() {
        return permissionRole;
    }
}
