package net.example.batchgateway.application.domain.model.keymodule;

import java.util.UUID;

public record KeyRevisionId(UUID value) {
    public static KeyRevisionId generate() {
        return new KeyRevisionId(UUID.randomUUID());
    }

    public String asString() {
        return value.toString();
    }
}

