package net.example.batchgateway.application.domain.model.keymodule;

import java.util.UUID;

public record KeyValueId(UUID value) {
    public static KeyValueId generate() {
        return new KeyValueId(UUID.randomUUID());
    }

    public String asString() {
        return value.toString();
    }

}
