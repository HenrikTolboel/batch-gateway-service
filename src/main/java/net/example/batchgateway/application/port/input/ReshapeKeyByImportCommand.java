package net.example.batchgateway.application.port.input;

import net.example.batchgateway.application.domain.model.keymodule.KeyId;
import net.example.batchgateway.application.domain.model.keymodule.KeyMaterialDetails;
import net.example.batchgateway.application.domain.model.tenantmodule.TenantId;
import net.example.batchgateway.application.domain.model.usermodule.UserId;

import java.time.Instant;
import java.util.Objects;

public record ReshapeKeyByImportCommand(
        TenantId tenantId,
        UserId userId,
        KeyId keyId,
        KeyMaterialDetails keyMaterialDetails,
        Instant expire,
        byte[] encryptedKeyMaterial,
        KeyFormat keyFormat,
        KeyId transportKeyId
) {
    public ReshapeKeyByImportCommand {
        Objects.requireNonNull(tenantId, "TenantId cannot be null");
        Objects.requireNonNull(userId, "UserId cannot be null");
        Objects.requireNonNull(keyId, "KeyId cannot be null");
        Objects.requireNonNull(keyMaterialDetails, "KeyMaterialDetails cannot be null");
        Objects.requireNonNull(expire, "expire cannot be null");
        Objects.requireNonNull(encryptedKeyMaterial, "encryptedKeyMaterial cannot be null");
        Objects.requireNonNull(keyFormat, "keyFormat cannot be null");
        Objects.requireNonNull(transportKeyId, "transportKeyId cannot be null");
    }

}
