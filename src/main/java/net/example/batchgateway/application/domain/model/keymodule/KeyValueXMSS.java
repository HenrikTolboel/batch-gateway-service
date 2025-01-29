package net.example.batchgateway.application.domain.model.keymodule;


import com.fasterxml.jackson.annotation.JsonProperty;
import net.example.batchgateway.application.domain.model.Entity;

import java.util.Objects;
import java.util.UUID;

public final class KeyValueXMSS extends Entity<UUID> implements KeyValueDetails {
    private final int size;
    private final KcvType kcvType;
    private final int kcvLength;
    private byte[] value;

    public KeyValueXMSS(final @JsonProperty("size") int size,
                        final @JsonProperty("kcvType") KcvType kcvType,
                        final @JsonProperty("kcvLength") int kcvLength,
                        final @JsonProperty("value") byte[] value) {
        super(UUID.randomUUID());
        if (size != 128 && size != 192 && size != 256) {
            throw new IllegalArgumentException("size must be 128 or 192 or 256");
        }
        Objects.requireNonNull(kcvType, "kcvType is null");
        if (kcvLength <= 0) {
            throw new IllegalArgumentException("kcvLength must be greater than 0");
        }
        Objects.requireNonNull(value, "value is null");

        this.size = size;
        this.kcvType = kcvType;
        this.kcvLength = kcvLength;
        this.value = value.clone();
    }

    private KeyValueXMSS(final KeyValueXMSS other) {
        super(other.getId());
        this.size = other.getSize();
        this.kcvType = other.getKcvType();
        this.kcvLength = other.getKcvLength();
        this.value = other.getValue();
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
    public KeyValueXMSS clone() {
        return new KeyValueXMSS(this);
    }

    public int getSize() {
        return size;
    }

    public KcvType getKcvType() {
        return kcvType;
    }

    public int getKcvLength() {
        return kcvLength;
    }

    public byte[] getValue() {
        return value.clone();
    }

    @Override
    public String toString() {
        return "KeyValueXMSS[" +
                "size=" + size + ", " +
                "kcvType=" + kcvType + ", " +
                "kcvLength=" + kcvLength + ", " +
                "value=" + value + ']';
    }

}
