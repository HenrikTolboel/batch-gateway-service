package net.example.batchgateway.application.port.output;

import net.example.batchgateway.application.domain.model.keymodule.KeyMaterialDetails;
import net.example.batchgateway.application.domain.model.tenantmodule.TenantId;

import java.util.Objects;

public record GenerateKeyMaterialCommand(TenantId tenantId, KeyMaterialDetails keyMaterialDetails) {

    public GenerateKeyMaterialCommand {
        Objects.requireNonNull(tenantId);
        Objects.requireNonNull(keyMaterialDetails);
    }

}
