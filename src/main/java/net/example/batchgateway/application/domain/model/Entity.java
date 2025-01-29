package net.example.batchgateway.application.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public abstract class Entity<ID> {

    private final ID id;

    protected Entity(final @JsonProperty("id") ID id) {
        Objects.requireNonNull(id, "id must not be null");
        this.id = id;
    }

    public ID getId() {
        return id;
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
        final Entity<?> that = (Entity<?>) o;
        return id.equals(that.id);
    }

}
