package net.example.batchgateway.application.port;

import net.example.batchgateway.application.domain.model.applicationmodule.ApplicationId;
import net.example.batchgateway.application.domain.model.Operation;
import net.example.batchgateway.application.domain.model.keymodule.KeyId;
import net.example.batchgateway.application.domain.model.keymodule.KeyType;
import net.example.batchgateway.application.port.input.PKCS11Mechanism;

public sealed interface DomainError extends GeneralError {
    record KeyNotFoundError(KeyId keyId) implements DomainError { }
    record KeyAlreadyExistError(KeyId keyId) implements DomainError { }
    record KeyReshapeError(KeyId keyId) implements DomainError { }
    record PKCS11ContextNotFoundError(ApplicationId applicationId, KeyId keyId) implements DomainError { }
    record CryptoProfileNotFoundError(KeyType keyType, Operation operation, PKCS11Mechanism mechanism) implements DomainError { }
}
