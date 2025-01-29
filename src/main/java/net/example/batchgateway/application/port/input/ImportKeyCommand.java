package net.example.batchgateway.application.port.input;

import net.example.batchgateway.application.domain.model.keymodule.KeyId;
import net.example.batchgateway.application.domain.model.keymodule.KeyMaterialDetails;
import net.example.batchgateway.application.domain.model.keymodule.KeyName;
import net.example.batchgateway.application.domain.model.keymodule.KeyType;
import net.example.batchgateway.application.domain.model.tenantmodule.TenantId;
import net.example.batchgateway.application.domain.model.usermodule.UserId;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public record ImportKeyCommand(
        TenantId tenantId,
        UserId userId,
        KeyName keyName,
        KeyType keyType, // TODO: what is basicKeyType compared to this?
        KeyMaterialDetails keyMaterialDetails,
        Instant expire,
        boolean autoActivate,
        boolean autoRotate,
        int keyLifeCycleDays,
        Map<String, Object> customAttributes,
        byte[] encryptedKeyMaterial,
        KeyFormat keyFormat,
        KeyId transportKeyId
) {
    public ImportKeyCommand {
        Objects.requireNonNull(tenantId, "TenantId cannot be null");
        Objects.requireNonNull(userId, "UserId cannot be null");
        Objects.requireNonNull(keyName, "KeyName cannot be null");
        Objects.requireNonNull(keyType, "KeyType cannot be null");
        Objects.requireNonNull(keyMaterialDetails, "KeyMaterialDetails cannot be null");
        Objects.requireNonNull(expire, "Expire cannot be null");
        if (keyLifeCycleDays < 1) {
            throw new IllegalArgumentException("KeyLifeCycleDays cannot be less than 1");
        }
        Objects.requireNonNull(encryptedKeyMaterial, "EncryptedKeyMaterial cannot be null");
        Objects.requireNonNull(keyFormat, "KeyFormat cannot be null");
        Objects.requireNonNull(transportKeyId, "TransportKeyId cannot be null");
    }
}
