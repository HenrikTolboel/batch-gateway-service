package net.example.batchgateway.application.domain.service;

import net.example.batchgateway.application.domain.model.events.DomainEvent;
import net.example.batchgateway.application.port.GeneralError;
import net.example.batchgateway.application.port.output.DomainEventRepositoryPort;
import net.example.utils.dichotomy.Result;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@EnableScheduling
public class DomainEventPublisher {

    private final ApplicationEventPublisher eventPublisher;
    private final DomainEventRepositoryPort domainEventRepository;

    private final AtomicBoolean active = new AtomicBoolean(false);

    public DomainEventPublisher(final ApplicationEventPublisher eventPublisher,
                                final DomainEventRepositoryPort domainEventRepository) {
        this.eventPublisher = eventPublisher;
        this.domainEventRepository = domainEventRepository;
    }

    @Scheduled(fixedDelayString = "${domainevent.publisher.delay:5000}", initialDelayString = "${domainevent.publisher.delay:5000}")
    public void publishDomainEvents() {
        if (active.compareAndSet(false, true)) {
            try {
                while (publishMoreDomainEvents()) {
                    // Empty
                }
            } finally {
                active.set(false);
            }
        }
    }

    private boolean publishMoreDomainEvents() {
        final Result<List<DomainEvent>, GeneralError> top10Results = domainEventRepository.findTop10DomainEvents();

        if (top10Results.isErr()) {
            return false;
        } else {
            final List<DomainEvent> domainEventList = top10Results.ok().orElse(List.of());
            domainEventList.forEach(eventPublisher::publishEvent);
            domainEventRepository.delete(domainEventList);
            return domainEventList.size() == 10;
        }
    }

}
