package net.example.batchgateway.application.domain.model;

import java.util.UUID;

public record BatchId(UUID value) {
    public static BatchId generate() {
        return new BatchId(UUID.randomUUID());
    }

}

