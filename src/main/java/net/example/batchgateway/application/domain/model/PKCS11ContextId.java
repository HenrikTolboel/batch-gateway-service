package net.example.batchgateway.application.domain.model;

import java.util.UUID;

public record PKCS11ContextId(UUID value) {
    public static PKCS11ContextId generate() {
        return new PKCS11ContextId(UUID.randomUUID());
    }

}

