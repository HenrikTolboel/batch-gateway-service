package net.example.batchgateway.adapter.output.batchrepository;

import net.example.batchgateway.application.domain.model.Batch;
import net.example.batchgateway.application.domain.model.BatchId;
import net.example.batchgateway.application.domain.model.events.BatchCreatedEvent;
import net.example.batchgateway.application.domain.model.events.BatchUpdatedEvent;
import net.example.batchgateway.application.domain.model.events.TenantCreatedEvent;
import net.example.batchgateway.application.domain.model.events.TenantUpdatedEvent;
import net.example.batchgateway.application.domain.model.tenantmodule.Tenant;
import net.example.batchgateway.application.domain.model.tenantmodule.TenantId;
import net.example.batchgateway.application.port.GeneralError;
import net.example.batchgateway.application.port.output.BatchRepositoryPort;
import net.example.batchgateway.application.port.output.TenantRepositoryPort;
import net.example.utils.dichotomy.Result;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
class MemoryBatchRepository implements BatchRepositoryPort {

    private final Map<BatchId, Batch> batches = new HashMap<>();

    public MemoryBatchRepository() {
    }

    @Override
    public Result<Optional<Batch>, GeneralError> findById(final BatchId batchId) {
        final Optional<Batch> result;
        if (batches.containsKey(batchId)) {
            result = Optional.of(batches.get(batchId));
        } else {
            result = Optional.empty();
        }
        return Result.ofOK(result);
    }

    @Override
    @Transactional
    public Result<Batch, GeneralError> save(final Batch batch) {
        final boolean existingBatch = batches.containsKey(batch.getId());
        batches.put(batch.getId(), batch);
        if (existingBatch) {
            batch.registerEvent(new BatchUpdatedEvent(batch.getId()));
        } else {
            batch.registerEvent(new BatchCreatedEvent(batch.getId()));
        }

        return Result.ofOK(batch);
    }
}
