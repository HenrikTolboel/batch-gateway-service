package net.example.batchgateway.application.domain.model;

import net.example.batchgateway.application.domain.model.events.BatchCreatedEvent;
import net.example.batchgateway.application.domain.model.tenantmodule.TenantId;

import java.util.Objects;

public final class Batch extends Aggregate<BatchId> {
    private final BatchName batchName;
    private final TenantId tenantId;

    private Batch(final BatchId batchId,
                  final BatchName batchName,
                  final TenantId tenantId) {
        super(batchId);
        Objects.requireNonNull(batchName, "batchName must not be null");
        Objects.requireNonNull(tenantId, "tenantId must not be null");
        this.batchName = batchName;
        this.tenantId = tenantId;
    }

    public static Batch initExisting(final BatchId batchId,
                                     final BatchName batchName,
                                     final TenantId tenantId) {
        return new Batch(batchId, batchName, tenantId);
    }

    public static Batch create(final BatchName batchName,
                               final TenantId tenantId) {
        final Batch batch = new Batch(BatchId.generate(), batchName, tenantId);

        batch.registerEvent(new BatchCreatedEvent(batch.getId()));

        return batch;
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public BatchName getBatchName() {
        return batchName;
    }

}
