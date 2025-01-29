package net.example.batchgateway.application.service;

import net.example.batchgateway.application.domain.model.keymodule.Key;
import net.example.batchgateway.application.domain.model.keymodule.KeyId;
import net.example.batchgateway.application.port.GeneralError;
import net.example.batchgateway.application.port.output.DomainEventRepositoryPort;
import net.example.batchgateway.application.port.output.KeyRepositoryPort;
import net.example.utils.dichotomy.Result;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
class KeyAdapter {

    private final KeyRepositoryPort keyRepository;
    private final DomainEventRepositoryPort domainEventRepository;

    public KeyAdapter(final KeyRepositoryPort keyRepository, final DomainEventRepositoryPort domainEventRepository) {
        this.keyRepository = keyRepository;
        this.domainEventRepository = domainEventRepository;
    }

    public Result<Optional<Key>, GeneralError> findById(final KeyId keyId) {
        return keyRepository.findById(keyId);
    }

    @Transactional
    public Result<Key, GeneralError> save(final Key key) {
        final Result<Key, GeneralError> result = keyRepository.save(key);
        if (result.isOK() && domainEventRepository.save(key.domainEvents()).isOK()) {
            key.clearDomainEvents();
        }

        return result;
    }

}
