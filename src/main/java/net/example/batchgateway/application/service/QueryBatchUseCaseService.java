package net.example.batchgateway.application.service;

import io.micrometer.observation.ObservationRegistry;
import net.example.batchgateway.application.domain.model.Batch;
import net.example.batchgateway.application.port.GeneralError;
import net.example.batchgateway.application.port.input.FindBatchQuery;
import net.example.batchgateway.application.port.input.QueryBatchUseCasePort;
import net.example.utils.dichotomy.Result;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
class QueryBatchUseCaseService implements QueryBatchUseCasePort {

    private final BatchAdapter batchAdapter;
    private final ObservationRegistry observationRegistry;

    public QueryBatchUseCaseService(final BatchAdapter batchAdapter,
                                    final ObservationRegistry observationRegistry) {
        this.batchAdapter = batchAdapter;
        this.observationRegistry = observationRegistry;
    }


    @Override
    public Result<Optional<Batch>, GeneralError> findBatchById(FindBatchQuery command) {

        // check allowance by tenantId and userId
        // find Batch via BatchAdapter
        // return Batch

        Result<Optional<Batch>, GeneralError> result = batchAdapter.findById(command.batchId());

        return result;
    }
}
