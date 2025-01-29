package net.example.batchgateway.application.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import net.example.batchgateway.application.domain.model.applicationmodule.ApplicationId;
import net.example.batchgateway.application.domain.model.keymodule.KeyId;

import java.util.Objects;

public final class PKCS11Context extends Aggregate<PKCS11ContextId> {
    private final KeyId keyId;
    private final ApplicationId applicationId;

    // TODO: Is there a uniqueness requirement on (ApplicationId, KeyId) ?

    private PKCS11Context(final PKCS11ContextId pkcs11ContextId, final KeyId keyId, final ApplicationId applicationId) {
        super(pkcs11ContextId);
        Objects.requireNonNull(keyId, "keyId is null");
        Objects.requireNonNull(applicationId, "applicationId is null");
        this.keyId = keyId;
        this.applicationId = applicationId;
    }

    @JsonCreator
    public static PKCS11Context initExisting(final PKCS11ContextId pkcs11ContextId, final KeyId keyId, final ApplicationId applicationId) {
        return new PKCS11Context(pkcs11ContextId, keyId, applicationId);
    }

    public static PKCS11Context create(final KeyId keyId, final ApplicationId applicationId) {
        return new PKCS11Context(PKCS11ContextId.generate(), keyId, applicationId);
    }

    public KeyId getKeyId() {
        return keyId;
    }

    public ApplicationId getApplicationId() {
        return applicationId;
    }
}
