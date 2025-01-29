package net.example.batchgateway.application.domain.model;

import java.util.Objects;

public class Name {

    private final String value;

    public Name(final String value) {
        Objects.requireNonNull(value);
        final String trimmed = value.trim();
        if (trimmed.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty or blank");
        }
        if (trimmed.length() > 255) {
            throw new IllegalArgumentException("Name cannot exceed 255 characters");
        }
        if (! trimmed.matches("^[A-Za-z0-9][\sA-Za-z0-9\\._-]*[A-Za-z0-9]$")) {
            throw new IllegalArgumentException("Name cannot contain special characters");
        }
        this.value = trimmed;
    }

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Name name1)) {
            return false;
        }

        return Objects.equals(value, name1.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {return value;}
}
