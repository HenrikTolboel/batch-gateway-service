package net.example.batchgateway.application.domain.model.tenantmodule;

import net.example.batchgateway.application.domain.model.Aggregate;
import net.example.batchgateway.application.domain.model.events.TenantCreatedEvent;
import net.example.batchgateway.application.domain.model.events.TenantUpdatedEvent;

import java.util.Objects;

public final class Tenant extends Aggregate<TenantId> {
    private final TenantName tenantName;
    private boolean active;

    private Tenant(final TenantId tenantId, final TenantName tenantName, final boolean active) {
        super(tenantId);
        Objects.requireNonNull(tenantName, "tenantName must not be null");
        this.tenantName = tenantName;
        this.active = active;
    }

    public static Tenant initExisting(final TenantId tenantId, final TenantName tenantName, final boolean active) {
        return new Tenant(tenantId, tenantName, active);
    }

    public static Tenant create(final TenantName tenantName, final boolean active) {
        final Tenant tenant = new Tenant(TenantId.generate(), tenantName, active);

        tenant.registerEvent(new TenantCreatedEvent(tenant.getId()));

        return tenant;
    }

    public TenantName getTenantName() {
        return tenantName;
    }

    public boolean isActive() {
        return active;
    }

    public void activate() {
        if (!active) {
            registerEvent(new TenantUpdatedEvent(getId()));
        }
        active = true;
    }

    public void deactivate() {
        if (active) {
            registerEvent(new TenantUpdatedEvent(getId()));
        }
        active = false;
    }
}
