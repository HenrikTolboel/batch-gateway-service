package net.example.batchgateway.application.domain.model.keymodule;


import com.fasterxml.jackson.annotation.JsonProperty;
import net.example.batchgateway.application.domain.model.Entity;

import java.util.Objects;
import java.util.UUID;

public final class KeyValueEC extends Entity<UUID> implements KeyValueDetails {
    private final Curve curve;
    private final byte[] fingerprint;
    private byte[] value;
    private final boolean doWeHavePrivateKey;

    public KeyValueEC(
            final @JsonProperty("curve") Curve curve,
            final @JsonProperty("fingerprint") byte[] fingerprint,
            final @JsonProperty("value") byte[] value,
            final @JsonProperty("doWeHavePrivateKey") boolean doWeHavePrivateKey
    ) {
        super(UUID.randomUUID());
        Objects.requireNonNull(curve, "curve must not be null");
        Objects.requireNonNull(fingerprint, "fingerprint must not be null");
        Objects.requireNonNull(value, "value must not be null");
        this.curve = curve;
        this.fingerprint = fingerprint.clone();
        this.value = value.clone();
        this.doWeHavePrivateKey = doWeHavePrivateKey;
    }

    private KeyValueEC(final KeyValueEC other) {
        super(other.getId());
        this.curve = other.getCurve();
        this.fingerprint = other.getFingerprint();
        this.value = other.getValue();
        this.doWeHavePrivateKey = other.doWeHavePrivateKey();
    }

    @Override
    public Cipher getCipher() {
        return null;
    }

    @Override
    public void destroyValue() {
        value = null;
    }

    @Override
    public KeyValueEC clone() {
        return new KeyValueEC(this);
    }

    public Curve getCurve() {
        return curve;
    }

    public byte[] getFingerprint() {
        return fingerprint.clone();
    }

    public byte[] getValue() {
        return value.clone();
    }

    public boolean doWeHavePrivateKey() {
        return doWeHavePrivateKey;
    }

    @Override
    public String toString() {
        return "KeyValueEC[" +
                "curve=" + curve + ", " +
                "fingerprint=" + fingerprint + ", " +
                "value=" + value + ", " +
                "doWeHavePrivateKey=" + doWeHavePrivateKey + ']';
    }

}
