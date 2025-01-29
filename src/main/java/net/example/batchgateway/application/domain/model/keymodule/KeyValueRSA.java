package net.example.batchgateway.application.domain.model.keymodule;


import com.fasterxml.jackson.annotation.JsonProperty;
import net.example.batchgateway.application.domain.model.Entity;

import java.util.Objects;
import java.util.UUID;

public final class KeyValueRSA extends Entity<UUID> implements KeyValueDetails {
    private final int size;
    private final byte[] fingerprint;
    private final int publicExponent;
    private final byte[] modulus;
    private byte[] privatekey;
    private final boolean doWeHavePrivateKey;

    public KeyValueRSA(final @JsonProperty("size") int size,
                       final @JsonProperty("fingerprint") byte[] fingerprint,
                       final @JsonProperty("publicExponent") int publicExponent,
                       final @JsonProperty("modulus") byte[] modulus,
                       final @JsonProperty("privatekey") byte[] privatekey,
                       final @JsonProperty("doWeHavePrivateKey") boolean doWeHavePrivateKey) {
        super(UUID.randomUUID());
        if (size != 1024 && size != 2048 && size != 3072 && size != 4096) {
            throw new IllegalArgumentException("Size must be 1024, 2048, 3072 or 4096");
        }
        Objects.requireNonNull(fingerprint, "fingerprint must not be null");

        if (publicExponent != 3 && publicExponent != 65_537) {
            throw new IllegalArgumentException("Public exponent must be 3 and 65537");
        }
        Objects.requireNonNull(modulus, "modulus must not be null");
        Objects.requireNonNull(privatekey, "privatekey must not be null");

        this.size = size;
        this.fingerprint = fingerprint.clone();
        this.publicExponent = publicExponent;
        this.modulus = modulus.clone();
        this.privatekey = privatekey.clone();
        this.doWeHavePrivateKey = doWeHavePrivateKey;
    }

    private KeyValueRSA(final KeyValueRSA other) {
        super(other.getId());
        this.size = other.getSize();
        this.fingerprint = other.getFingerprint();
        this.publicExponent = other.getPublicExponent();
        this.modulus = other.getModulus();
        this.privatekey = other.getPrivatekey();
        this.doWeHavePrivateKey = other.doWeHavePrivateKey();
    }

    @Override
    public Cipher getCipher() {
        return null;
    }

    @Override
    public void destroyValue() {
        privatekey = null;  // TODO: what to destroy??
    }

    @Override
    public KeyValueRSA clone() {
        return new KeyValueRSA(this);
    }

    public int getSize() {
        return size;
    }

    public byte[] getFingerprint() {
        return fingerprint.clone();
    }

    public int getPublicExponent() {
        return publicExponent;
    }

    public byte[] getModulus() {
        return modulus.clone();
    }

    public byte[] getPrivatekey() {
        return privatekey.clone();
    }

    public boolean doWeHavePrivateKey() {
        return doWeHavePrivateKey;
    }

    @Override
    public String toString() {
        return "KeyValueRSA[" +
                "size=" + size + ", " +
                "fingerprint=" + fingerprint + ", " +
                "publicExponent=" + publicExponent + ", " +
                "modulus=" + modulus + ", " +
                "privatekey=" + privatekey + ", " +
                "doWeHavePrivateKey=" + doWeHavePrivateKey + ']';
    }

}
