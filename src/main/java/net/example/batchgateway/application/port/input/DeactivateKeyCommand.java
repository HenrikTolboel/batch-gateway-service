package net.example.batchgateway.application.port.input;

import net.example.batchgateway.application.domain.model.keymodule.KeyId;
import net.example.batchgateway.application.domain.model.tenantmodule.TenantId;
import net.example.batchgateway.application.domain.model.usermodule.UserId;

import java.util.Objects;

public record DeactivateKeyCommand(TenantId tenantId, UserId userId, KeyId keyId) {
    public DeactivateKeyCommand {
        Objects.requireNonNull(tenantId);
        Objects.requireNonNull(userId);
        Objects.requireNonNull(keyId);
    }
}
