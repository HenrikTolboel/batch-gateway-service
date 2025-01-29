package net.example.batchgateway.application.domain.model;

public record Data(String value) {

    public Data {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Value cannot be null or empty");
        }
    }
}
