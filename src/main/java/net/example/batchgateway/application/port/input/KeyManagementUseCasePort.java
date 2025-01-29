package net.example.batchgateway.application.port.input;

import net.example.batchgateway.application.domain.model.keymodule.KeyId;
import net.example.batchgateway.application.port.GeneralError;
import net.example.utils.dichotomy.Empty;
import net.example.utils.dichotomy.Result;

public interface KeyManagementUseCasePort {

    Result<Empty, GeneralError> activateKey(final ActivateKeyCommand command);

    Result<Empty, GeneralError> deactivateKey(final DeactivateKeyCommand command);

    Result<Empty, GeneralError> deactivatePreviousKey(final DeactivatePreviousKeyCommand command);

    Result<KeyId, GeneralError> destroyKey(final DestroyKeyCommand command);

    Result<KeyId, GeneralError> destroySpecificKey(final DestroySpecificKeyCommand command);

    Result<KeyId, GeneralError> rotateKey(final RotateKeyCommand command);

    Result<KeyId, GeneralError> modifyKey(final ModifyKeyCommand command);

    Result<KeyId, GeneralError> promoteKey(final PromoteKeyCommand command);
}
