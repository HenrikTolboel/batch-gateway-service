package net.example.batchgateway.application.port.input;

import net.example.batchgateway.application.domain.model.keymodule.Key;
import net.example.batchgateway.application.port.GeneralError;
import net.example.utils.dichotomy.Result;

import java.util.Optional;

public interface ReshapeKeyUseCasePort {

    Result<Optional<Key>, GeneralError> reshape(final ReshapeKeyByGenerateCommand command);

    Result<Optional<Key>, GeneralError> reshape(final ReshapeKeyByImportCommand command);

}
