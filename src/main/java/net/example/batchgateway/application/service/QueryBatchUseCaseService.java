package net.example.batchgateway.application.service;

import io.micrometer.observation.ObservationRegistry;
import net.example.batchgateway.application.domain.model.Batch;
import net.example.batchgateway.application.port.GeneralError;
import net.example.batchgateway.application.port.input.CreateBatchCommand;
import net.example.batchgateway.application.port.input.CreateBatchUseCasePort;
import net.example.batchgateway.application.port.input.FindBatchQuery;
import net.example.batchgateway.application.port.input.QueryBatchUseCasePort;
import net.example.batchgateway.application.port.output.BatchRepositoryPort;
import net.example.utils.dichotomy.Result;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
class QueryBatchUseCaseService implements QueryBatchUseCasePort {

    private final BatchRepositoryPort batchRepository;
    private final ObservationRegistry observationRegistry;


    public QueryBatchUseCaseService(final BatchRepositoryPort batchRepository,
                                    final ObservationRegistry observationRegistry) {
        this.batchRepository = batchRepository;
        this.observationRegistry = observationRegistry;
    }


    @Override
    public Result<Optional<Batch>, GeneralError> findBatchById(FindBatchQuery command) {
        return batchRepository.findById(command.batchId());
    }
}
