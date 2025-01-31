package net.example.batchgateway.application.port.input;

import net.example.batchgateway.application.domain.model.Batch;
import net.example.batchgateway.application.domain.model.keymodule.Key;
import net.example.batchgateway.application.port.GeneralError;
import net.example.utils.dichotomy.Result;

import java.util.Optional;

public interface QueryBatchUseCasePort {

    Result<Optional<Batch>, GeneralError> findBatchById(final FindBatchQuery command);


}
