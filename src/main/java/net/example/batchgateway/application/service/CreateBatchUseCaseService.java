package net.example.batchgateway.application.service;

import io.micrometer.observation.ObservationRegistry;
import net.example.batchgateway.application.domain.model.Batch;
import net.example.batchgateway.application.port.GeneralError;
import net.example.batchgateway.application.port.input.CreateBatchCommand;
import net.example.batchgateway.application.port.input.CreateBatchUseCasePort;
import net.example.utils.dichotomy.Result;
import org.springframework.stereotype.Service;

@Service
class CreateBatchUseCaseService implements CreateBatchUseCasePort {

    private final BatchAdapter batchAdapter;
    private final ObservationRegistry observationRegistry;


    public CreateBatchUseCaseService(final BatchAdapter batchAdapter,
                                     final ObservationRegistry observationRegistry) {
        this.batchAdapter = batchAdapter;
        this.observationRegistry = observationRegistry;
    }

    @Override
    public Result<Batch, GeneralError> create(final CreateBatchCommand command) {

// Prerequisites
//  The tenant must be enabled to allow use case
//  The user must be allowed to generate a new Key for tenant

// * Create new Key with properties from command
// * Generate key material
// * Create new Key Value with the generated key material and state pre-active and origin GENERATED
// * Create new Key Revision with link to key value and custom attributes from command


        Batch batch = Batch.create(command.batchName(), command.tenantId());

        // SAVE

        return Result.ofOK(batch);
    }

}
