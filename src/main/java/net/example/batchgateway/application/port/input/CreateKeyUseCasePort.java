package net.example.batchgateway.application.port.input;

import net.example.batchgateway.application.domain.model.keymodule.Key;
import net.example.batchgateway.application.port.GeneralError;
import net.example.utils.dichotomy.Result;

public interface CreateKeyUseCasePort {

    Result<Key, GeneralError> create(final GenerateKeyCommand command);

    Result<Key, GeneralError> create(final GenerateKeyWithIdCommand command);

    Result<Key, GeneralError> create(final ImportKeyCommand command);

}
