package net.example.batchgateway.application.port.output;

import net.example.batchgateway.application.domain.model.events.DomainEvent;
import net.example.batchgateway.application.port.GeneralError;
import net.example.utils.dichotomy.Empty;
import net.example.utils.dichotomy.Result;

import java.util.List;

public interface DomainEventRepositoryPort {
    Result<Empty, GeneralError> save(final DomainEvent domainEvent);

    Result<Empty, GeneralError> save(final List<DomainEvent> domainEvents);

    Result<Empty, GeneralError> delete(final List<DomainEvent> domainEvents);

    Result<List<DomainEvent>, GeneralError> findTop10DomainEvents();



}
