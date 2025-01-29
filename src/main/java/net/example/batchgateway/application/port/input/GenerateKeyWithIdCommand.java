package net.example.batchgateway.application.port.input;

import net.example.batchgateway.application.domain.model.keymodule.CustomAttributes;
import net.example.batchgateway.application.domain.model.keymodule.KeyMaterialDetails;
import net.example.batchgateway.application.domain.model.keymodule.KeyName;
import net.example.batchgateway.application.domain.model.keymodule.KeyType;
import net.example.batchgateway.application.domain.model.tenantmodule.TenantId;
import net.example.batchgateway.application.domain.model.usermodule.UserId;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record GenerateKeyWithIdCommand(
        UUID id,
        TenantId tenantId,
        UserId userId,
        KeyName keyName,
        KeyType keyType, // TODO: what is basicKeyType compared to this?
        KeyMaterialDetails keyMaterialDetails,
        Instant expire,
        boolean autoActivate,
        boolean autoRotate,
        int keyLifeCycleDays,
        CustomAttributes customAttributes
) {
    public GenerateKeyWithIdCommand {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(tenantId, "TenantId cannot be null");
        Objects.requireNonNull(userId, "UserId cannot be null");
        Objects.requireNonNull(keyName, "KeyName cannot be null");
        Objects.requireNonNull(keyType, "KeyType cannot be null");
        Objects.requireNonNull(keyMaterialDetails, "KeyMaterialDetails cannot be null");
        Objects.requireNonNull(expire, "Expire cannot be null");
        if (keyLifeCycleDays < 1) {
            throw new IllegalArgumentException("KeyLifeCycleDays cannot be less than 1");
        }
    }
}
