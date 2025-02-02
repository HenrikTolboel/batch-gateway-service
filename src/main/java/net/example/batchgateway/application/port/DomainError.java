package net.example.batchgateway.application.port;

import net.example.batchgateway.application.domain.model.keymodule.KeyId;

public sealed interface DomainError extends GeneralError {
    record KeyNotFoundError(KeyId keyId) implements DomainError { }
    record KeyAlreadyExistError(KeyId keyId) implements DomainError { }
    record KeyReshapeError(KeyId keyId) implements DomainError { }
}
