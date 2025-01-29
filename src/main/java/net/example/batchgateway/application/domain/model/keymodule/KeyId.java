package net.example.batchgateway.application.domain.model.keymodule;

import java.util.UUID;

public record KeyId(UUID value) {
    public static KeyId generate() {
        return new KeyId(UUID.randomUUID());
    }

}

