package net.example.batchgateway.application.port.output;

import net.example.batchgateway.application.domain.model.Batch;
import net.example.batchgateway.application.domain.model.BatchId;
import net.example.batchgateway.application.port.GeneralError;
import net.example.utils.dichotomy.Result;

import java.util.Optional;

public interface BatchRepositoryPort {
    Result<Optional<Batch>, GeneralError> findById(final BatchId batchId);

    Result<Batch, GeneralError> save(final Batch batch);
}
