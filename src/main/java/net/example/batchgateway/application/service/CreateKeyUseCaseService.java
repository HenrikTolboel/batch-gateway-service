package net.example.batchgateway.application.service;

import io.micrometer.observation.ObservationRegistry;
import net.example.batchgateway.application.domain.model.keymodule.CustomAttributes;
import net.example.batchgateway.application.domain.model.keymodule.Key;
import net.example.batchgateway.application.domain.model.keymodule.KeyOrigin;
import net.example.batchgateway.application.domain.model.keymodule.KeyRestrictions;
import net.example.batchgateway.application.domain.model.keymodule.KeyRevision;
import net.example.batchgateway.application.domain.model.keymodule.KeyValue;
import net.example.batchgateway.application.port.GeneralError;
import net.example.batchgateway.application.port.input.CreateKeyUseCasePort;
import net.example.batchgateway.application.port.input.GenerateKeyCommand;
import net.example.batchgateway.application.port.input.GenerateKeyWithIdCommand;
import net.example.batchgateway.application.port.input.ImportKeyCommand;
import net.example.batchgateway.application.port.output.GenerateKeyMaterialCommand;
import net.example.batchgateway.application.port.output.GenerateKeyMaterialPort;
import net.example.utils.dichotomy.Result;
import org.springframework.stereotype.Service;

@Service
class CreateKeyUseCaseService implements CreateKeyUseCasePort {

    private final KeyAdapter keyAdapter;
    private final TenantAdapter tenantAdapter;
    private final GenerateKeyMaterialPort generateKeyMaterial;
    private final ObservationRegistry observationRegistry;


    public CreateKeyUseCaseService(final KeyAdapter keyAdapter,
                                   final TenantAdapter tenantAdapter,
                                   final GenerateKeyMaterialPort generateKeyMaterial,
                                   final ObservationRegistry observationRegistry) {
        this.keyAdapter = keyAdapter;
        this.tenantAdapter = tenantAdapter;
        this.generateKeyMaterial = generateKeyMaterial;
        this.observationRegistry = observationRegistry;
    }

    @Override
    public Result<Key, GeneralError> create(final GenerateKeyCommand command) {

// Prerequisites
//  The tenant must be enabled to allow use case
//  The user must be allowed to generate a new Key for tenant

// * Create new Key with properties from command
// * Generate key material
// * Create new Key Value with the generated key material and state pre-active and origin GENERATED
// * Create new Key Revision with link to key value and custom attributes from command


        return
                generateKeyMaterial.generateKeyMaterial(new GenerateKeyMaterialCommand(command.tenantId(), command.keyMaterialDetails()))
                        .flatMap(details -> {
                            final KeyValue keyValue = KeyValue.create(command.expire(), new KeyRestrictions(123, true), new KeyOrigin("GENERATED"), details);

                            final KeyRevision keyRevision = KeyRevision.create(keyValue.getId(), CustomAttributes.empty());

                            final Key key = Key.create(command.tenantId(),
                                    command.keyName(),
                                    true,
                                    command.keyType(),
                                    command.keyLifeCycleDays(),
                                    command.autoRotate(),
                                    command.autoActivate());

                            key.add(keyValue);
                            key.add(keyRevision);

                            return keyAdapter.save(key);
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
    public Result<Key, GeneralError> create(GenerateKeyWithIdCommand command) {
        return null;
    }

    @Override
    public Result<Key, GeneralError> create(final ImportKeyCommand command) {
        return null;
    }
}
