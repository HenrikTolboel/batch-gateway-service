package net.example.batchgateway.application.service;

import io.micrometer.observation.ObservationRegistry;
import net.example.batchgateway.application.domain.model.keymodule.Key;
import net.example.batchgateway.application.port.GeneralError;
import net.example.batchgateway.application.port.input.FindKeyQuery;
import net.example.batchgateway.application.port.input.QueryKeyUseCasePort;
import net.example.utils.dichotomy.Result;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
class QueryKeyUseCaseService implements QueryKeyUseCasePort {

    private final KeyAdapter keyAdapter;
    private final TenantAdapter tenantAdapter;
    private final ObservationRegistry observationRegistry;

    public QueryKeyUseCaseService(final KeyAdapter keyAdapter,
                                  final TenantAdapter tenantAdapter,
                                  final ObservationRegistry observationRegistry) {
        this.keyAdapter = keyAdapter;
        this.tenantAdapter = tenantAdapter;
        this.observationRegistry = observationRegistry;
    }


    @Override
    public Result<Optional<Key>, GeneralError> findKeyById(FindKeyQuery command) {

        // check allowance by tenantId and userId
        // find Key via KeyAdapter
        // return key

        Result<Optional<Key>, GeneralError> result = keyAdapter.findById(command.keyId());

        return result;
    }
}
