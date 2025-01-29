package net.example.batchgateway.application.domain.model.usermodule;

import net.example.batchgateway.application.domain.model.Aggregate;
import net.example.batchgateway.application.domain.model.events.UserCreatedEvent;
import net.example.batchgateway.application.domain.model.events.UserPermissionAddedEvent;
import net.example.batchgateway.application.domain.model.events.UserPermissionRemovedEvent;
import net.example.batchgateway.application.domain.model.tenantmodule.TenantId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class User extends Aggregate<UserId> {
    private final UserName userName;
    private final TenantId tenantId;

    private final List<PermissionRole> permissionRoles = new ArrayList<>();

    private User(final UserId userId, final UserName userName, final TenantId tenantId) {
        super(userId);
        Objects.requireNonNull(userName, "userName cannot be null");
        Objects.requireNonNull(tenantId, "tenantId cannot be null");
        this.userName = userName;
        this.tenantId = tenantId;
    }

    public static User initExisting(final UserId userId, final UserName userName, final TenantId tenantId) {
        final User user = new User(userId, userName, tenantId);

        return user;
    }

    public static User create(final UserName userName, final TenantId tenantId) {
        final User user = new User(UserId.generate(), userName, tenantId);

        user.registerEvent(new UserCreatedEvent(user.getId()));

        return user;
    }

    public void add(final PermissionRole permissionRole) {
        if (! permissionRoles.contains(permissionRole)) {
            permissionRoles.add(permissionRole);
            registerEvent(new UserPermissionAddedEvent(getId(), permissionRole));
        }

    }

    public void remove(final PermissionRole permissionRole) {
        if (permissionRoles.contains(permissionRole)) {
            permissionRoles.remove(permissionRole);
            registerEvent(new UserPermissionRemovedEvent(getId(), permissionRole));
        }
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public UserName getUserName() {
        return userName;
    }

    public boolean hasPermissionRole(final PermissionRole permissionRole) {
        return permissionRoles.contains(permissionRole);
    }

    public boolean hasPermissionRoles(final List<PermissionRole> permissionRoles) {
        return this.permissionRoles.containsAll(permissionRoles);
    }
}
