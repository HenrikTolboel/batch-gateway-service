package net.example.batchgateway.application.service;

import io.micrometer.observation.ObservationRegistry;
import net.example.batchgateway.application.domain.model.keymodule.Key;
import net.example.batchgateway.application.domain.model.keymodule.KeyOrigin;
import net.example.batchgateway.application.domain.model.keymodule.KeyRestrictions;
import net.example.batchgateway.application.domain.model.keymodule.KeyValue;
import net.example.batchgateway.application.port.DomainError;
import net.example.batchgateway.application.port.GeneralError;
import net.example.batchgateway.application.port.input.ReshapeKeyByGenerateCommand;
import net.example.batchgateway.application.port.input.ReshapeKeyByImportCommand;
import net.example.batchgateway.application.port.input.ReshapeKeyUseCasePort;
import net.example.batchgateway.application.port.output.GenerateKeyMaterialCommand;
import net.example.batchgateway.application.port.output.GenerateKeyMaterialPort;
import net.example.utils.dichotomy.Result;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
class ReshapeKeyUseCaseService implements ReshapeKeyUseCasePort {

    private final KeyAdapter keyAdapter;
    private final TenantAdapter tenantAdapter;
    private final GenerateKeyMaterialPort generateKeyMaterial;
    private final ObservationRegistry observationRegistry;

    public ReshapeKeyUseCaseService(final KeyAdapter keyAdapter,
                                    final TenantAdapter tenantAdapter,
                                    final GenerateKeyMaterialPort generateKeyMaterial,
                                    final ObservationRegistry observationRegistry) {
        this.keyAdapter = keyAdapter;
        this.tenantAdapter = tenantAdapter;
        this.generateKeyMaterial = generateKeyMaterial;
        this.observationRegistry = observationRegistry;
    }

    @Override
    public Result<Optional<Key>, GeneralError> reshape(final ReshapeKeyByGenerateCommand command) {

//        What
//        Reshape the key by creating a revision and new key value based on the values from the request
//
//        Reshape key to have the key value generated by CK360 (don't care what origin was before)
//
//        How
//        * Find key from keyId
//        * Find current revision and current key value
//        * Generate new key material based on command
//        * Create new Key Value based on current key value and the properties from the command.
//        * Set value in the Key Value to the generated key material, state to pre-active, and origin to GENERATED
//        * Create new Key Revision as copy of current revision with link to new key value
//        * Add new key revision and key value to key
//        * Save key

//        Generates DomainEvents

        Result<Optional<Key>, GeneralError> keyRes = keyAdapter.findById(command.keyId());

        if (keyRes.isErr()) {
            return Result.ofErr(keyRes.err().get());
        }
        if (keyRes.expect().isEmpty()) {
            return Result.ofOK(Optional.empty());
        }
        Key key = keyRes.expect().get();

        if (key.currentKeyRevision().isEmpty()) {
            return Result.ofErr(new DomainError.KeyNotFoundError(key.getId()));
        }


        return
                generateKeyMaterial.generateKeyMaterial(new GenerateKeyMaterialCommand(command.tenantId(),
                                command.keyMaterialDetails()))
                        .flatMap(details -> {
                            final KeyValue keyValue = KeyValue.create(command.expire(),
                                    new KeyRestrictions(123, true),
                                    new KeyOrigin("GENERATED"),
                                    details);

                            if (! key.reshape(keyValue)) {
                                Result.ofErr(new DomainError.KeyReshapeError(key.getId()));
                            }

                            return keyAdapter.save(key);
                        }).flatMap(savedKey -> {
                            return Result.ofOK(Optional.of(savedKey));
                        }).mapErr(err -> {

                            return err;
//                    return switch (err) {
//                        default -> {
//                            Result.ofErr(err);
//                        }
//                    }
                        });
    }

    @Override
    public Result<Optional<Key>, GeneralError> reshape(final ReshapeKeyByImportCommand command) {
        return null;
    }
}
