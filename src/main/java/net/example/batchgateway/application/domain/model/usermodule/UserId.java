package net.example.batchgateway.application.domain.model.usermodule;

import java.util.UUID;

public record UserId(UUID value) {
    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

}

