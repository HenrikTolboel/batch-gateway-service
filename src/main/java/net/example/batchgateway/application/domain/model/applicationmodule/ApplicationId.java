package net.example.batchgateway.application.domain.model.applicationmodule;

import java.util.UUID;

public record ApplicationId(UUID value) {
    public static ApplicationId generate() {
        return new ApplicationId(UUID.randomUUID());
    }

}

