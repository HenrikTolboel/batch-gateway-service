package net.example.batchgateway.application.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.example.batchgateway.application.domain.model.events.DomainEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class Aggregate<ID> {
    private final ID id;

    protected Aggregate(final @JsonProperty("id") ID id) {
        Objects.requireNonNull(id, "id must not be null");
        this.id = id;
    }

    private final transient List<DomainEvent> domainEvents = new ArrayList<>();

    public ID getId() {
        return id;
    }

    public DomainEvent registerEvent(final DomainEvent event) {

        if (event == null) {
            throw new IllegalArgumentException("Domain event must not be null");
        }

        this.domainEvents.add(event);
        return event;
    }

    /**
     * Clears all domain events currently held. Usually invoked by the infrastructure in place in Spring Data
     * repositories.
     */
    public void clearDomainEvents() {
        this.domainEvents.clear();
    }

    /**
     * All domain events currently captured by the aggregate.
     */
    public List<DomainEvent> domainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }


    @Override
    public final int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Aggregate<?> that = (Aggregate<?>) o;
        return id.equals(that.id);
    }

}
