package net.example.batchgateway.application.service;

import net.example.batchgateway.application.domain.model.Batch;
import net.example.batchgateway.application.domain.model.BatchId;
import net.example.batchgateway.application.port.GeneralError;
import net.example.batchgateway.application.port.output.BatchRepositoryPort;
import net.example.batchgateway.application.port.output.DomainEventRepositoryPort;
import net.example.utils.dichotomy.Result;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
class BatchAdapter {

    private final BatchRepositoryPort batchRepository;
    private final DomainEventRepositoryPort domainEventRepository;

    public BatchAdapter(final BatchRepositoryPort batchRepository, final DomainEventRepositoryPort domainEventRepository) {
        this.batchRepository = batchRepository;
        this.domainEventRepository = domainEventRepository;
    }

    public Result<Optional<Batch>, GeneralError> findById(final BatchId batchId) {
        return batchRepository.findById(batchId);
    }

    @Transactional
    public Result<Batch, GeneralError> save(final Batch batch) {
        final Result<Batch, GeneralError> result = batchRepository.save(batch);
        if (result.isOK() && domainEventRepository.save(batch.domainEvents()).isOK()) {
            batch.clearDomainEvents();
        }

        return result;
    }

}
