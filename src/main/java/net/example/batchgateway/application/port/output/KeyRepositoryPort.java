package net.example.batchgateway.application.port.output;

import net.example.batchgateway.application.domain.model.keymodule.Key;
import net.example.batchgateway.application.domain.model.keymodule.KeyId;
import net.example.batchgateway.application.port.GeneralError;
import net.example.utils.dichotomy.Result;

import java.util.Optional;

public interface KeyRepositoryPort {
    Result<Optional<Key>, GeneralError> findById(final KeyId keyId);

    Result<Key, GeneralError> save(final Key key);
}
