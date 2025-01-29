package net.example.batchgateway.application.domain.model;

import java.util.Objects;

public final class NameUtils {

    private NameUtils() {}

    public static String validName(final String value) {
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
        return trimmed;
    }

}
