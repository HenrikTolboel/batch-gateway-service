package net.example.batchgateway.application.port.input;

import net.example.batchgateway.application.domain.model.BatchId;
import net.example.batchgateway.application.domain.model.BatchName;
import net.example.batchgateway.application.domain.model.keymodule.CustomAttributes;
import net.example.batchgateway.application.domain.model.keymodule.KeyMaterialDetails;
import net.example.batchgateway.application.domain.model.keymodule.KeyName;
import net.example.batchgateway.application.domain.model.keymodule.KeyType;
import net.example.batchgateway.application.domain.model.tenantmodule.TenantId;
import net.example.batchgateway.application.domain.model.usermodule.UserId;

import java.time.Instant;
import java.util.Objects;

public record CreateBatchCommand(
        BatchId batchId,
        BatchName batchName,
        TenantId tenantId
) {
    public CreateBatchCommand {
        Objects.requireNonNull(batchId, "BatchId cannot be null");
        Objects.requireNonNull(batchName, "BatchName cannot be null");
        Objects.requireNonNull(tenantId, "TenantId cannot be null");
    }
}
