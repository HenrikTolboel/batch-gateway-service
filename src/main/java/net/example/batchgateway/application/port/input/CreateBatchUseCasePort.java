package net.example.batchgateway.application.port.input;

import net.example.batchgateway.application.domain.model.Batch;
import net.example.batchgateway.application.port.GeneralError;
import net.example.utils.dichotomy.Result;

public interface CreateBatchUseCasePort {

    Result<Batch, GeneralError> create(final CreateBatchCommand command);


}
