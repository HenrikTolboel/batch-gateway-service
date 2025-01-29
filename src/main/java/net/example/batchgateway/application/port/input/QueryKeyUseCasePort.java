package net.example.batchgateway.application.port.input;

import net.example.batchgateway.application.domain.model.keymodule.Key;
import net.example.batchgateway.application.port.GeneralError;
import net.example.utils.dichotomy.Result;

import java.util.Optional;

public interface QueryKeyUseCasePort {

    Result<Optional<Key>, GeneralError> findKeyById(final FindKeyQuery command);


}
