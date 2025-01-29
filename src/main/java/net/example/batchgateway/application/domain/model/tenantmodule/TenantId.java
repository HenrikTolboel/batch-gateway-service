package net.example.batchgateway.application.domain.model.tenantmodule;

import java.util.UUID;

public record TenantId(UUID value) {
    public static TenantId generate() {
        return new TenantId(UUID.randomUUID());
    }

}

