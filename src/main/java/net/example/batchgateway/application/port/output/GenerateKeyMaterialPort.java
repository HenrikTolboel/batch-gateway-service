package net.example.batchgateway.application.port.output;

import net.example.batchgateway.application.domain.model.keymodule.KeyValueDetails;
import net.example.batchgateway.application.port.GeneralError;
import net.example.utils.dichotomy.Result;

public interface GenerateKeyMaterialPort {

    Result<KeyValueDetails, GeneralError> generateKeyMaterial(final GenerateKeyMaterialCommand command);
}
