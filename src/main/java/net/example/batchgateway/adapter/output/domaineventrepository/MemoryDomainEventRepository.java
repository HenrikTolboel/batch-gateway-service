package net.example.batchgateway.adapter.output.domaineventrepository;

import net.example.batchgateway.application.domain.model.events.DomainEvent;
import net.example.batchgateway.application.port.GeneralError;
import net.example.batchgateway.application.port.output.DomainEventRepositoryPort;
import net.example.utils.dichotomy.Empty;
import net.example.utils.dichotomy.Result;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.min;

@Component
@ConditionalOnProperty(name = "storage.service", havingValue = "memory", matchIfMissing = true)
class MemoryDomainEventRepository implements DomainEventRepositoryPort {

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    @Override
    public Result<Empty, GeneralError> save(final DomainEvent domainEvent) {
        domainEvents.add(domainEvent);

        return Result.ofOK();
    }

    @Override
    public Result<Empty, GeneralError> save(final List<DomainEvent> domainEvents) {
        domainEvents.addAll(domainEvents);

        return Result.ofOK();
    }

    @Override
    public Result<Empty, GeneralError> delete(final List<DomainEvent> domainEvents) {
        domainEvents.removeAll(domainEvents);
        return Result.ofOK();
    }

    @Override
    public Result<List<DomainEvent>, GeneralError> findTop10DomainEvents() {
        return Result.ofOK(domainEvents.subList(0, min(domainEvents.size(), 10)));
    }
}
