package net.example.batchgateway.application.service;

import io.micrometer.observation.ObservationRegistry;
import net.example.batchgateway.application.domain.model.keymodule.Key;
import net.example.batchgateway.application.domain.model.keymodule.KeyId;
import net.example.batchgateway.application.port.GeneralError;
import net.example.batchgateway.application.port.input.ActivateKeyCommand;
import net.example.batchgateway.application.port.input.DeactivateKeyCommand;
import net.example.batchgateway.application.port.input.DeactivatePreviousKeyCommand;
import net.example.batchgateway.application.port.input.DestroyKeyCommand;
import net.example.batchgateway.application.port.input.DestroySpecificKeyCommand;
import net.example.batchgateway.application.port.input.KeyManagementUseCasePort;
import net.example.batchgateway.application.port.input.ModifyKeyCommand;
import net.example.batchgateway.application.port.input.PromoteKeyCommand;
import net.example.batchgateway.application.port.input.RotateKeyCommand;
import net.example.utils.dichotomy.Empty;
import net.example.utils.dichotomy.Result;
import org.springframework.stereotype.Service;

@Service
class KeyManagementUseCaseService implements KeyManagementUseCasePort {

    private final KeyAdapter keyAdapter;
    private final TenantAdapter tenantAdapter;
    private final ObservationRegistry observationRegistry;

    public KeyManagementUseCaseService(final KeyAdapter keyAdapter,
                                       final TenantAdapter tenantAdapter,
                                       final ObservationRegistry observationRegistry) {
        this.keyAdapter = keyAdapter;
        this.tenantAdapter = tenantAdapter;
        this.observationRegistry = observationRegistry;
    }

    @Override
    public Result<Empty, GeneralError> activateKey(final ActivateKeyCommand command) {
        return keyAdapter.findById(command.keyId())
                .flatMap(optionalKey -> {
                    if (optionalKey.isPresent()) {
                        final Key key = optionalKey.get();
                        if (key.activate()) {
                            final var r = keyAdapter.save(key);
                            if (r.isErr()) {
                                return Result.ofErr(r.err().get());
                            }
                        }
                    }
                    return Result.ofOK();
                })
                .mapErr(err -> err);
    }

    @Override
    public Result<Empty, GeneralError> deactivateKey(final DeactivateKeyCommand command) {
        return keyAdapter.findById(command.keyId())
                .flatMap(optionalKey -> {
                    if (optionalKey.isPresent()) {
                        final Key key = optionalKey.get();
                        if (key.deactivate()) {
                            final var r = keyAdapter.save(key);
                            if (r.isErr()) {
                                return Result.ofErr(r.err().get());
                            }
                        }
                    }
                    return Result.ofOK();
                })
                .mapErr(err -> err);
    }

    @Override
    public Result<Empty, GeneralError> deactivatePreviousKey(final DeactivatePreviousKeyCommand command) {
        return keyAdapter.findById(command.keyId())
                .flatMap(optionalKey -> {
                    if (optionalKey.isPresent()) {
                        final Key key = optionalKey.get();
                        if (key.deactivatePreviousKey()) {
                            final Result<Key, GeneralError> r = keyAdapter.save(key);
                            if (r.isErr()) {
                                return Result.ofErr(r.err().get());
                            }
                        }
                    }
                    return Result.ofOK();
                })
                .mapErr(err -> err);
    }

    @Override
    public Result<KeyId, GeneralError> destroyKey(final DestroyKeyCommand command) {
        return null;
    }

    @Override
    public Result<KeyId, GeneralError> destroySpecificKey(final DestroySpecificKeyCommand command) {
        return null;
    }

    @Override
    public Result<KeyId, GeneralError> rotateKey(final RotateKeyCommand command) {
        return null;
    }

    @Override
    public Result<KeyId, GeneralError> modifyKey(final ModifyKeyCommand command) {
        return null;
    }

    @Override
    public Result<KeyId, GeneralError> promoteKey(final PromoteKeyCommand command) {
        return null;
    }
}
