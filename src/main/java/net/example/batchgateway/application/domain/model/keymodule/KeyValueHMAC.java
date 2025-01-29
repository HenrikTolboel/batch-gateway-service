package net.example.batchgateway.application.domain.model.keymodule;


import com.fasterxml.jackson.annotation.JsonProperty;
import net.example.batchgateway.application.domain.model.Entity;

import java.util.Objects;
import java.util.UUID;

public final class KeyValueHMAC extends Entity<UUID> implements KeyValueDetails {
    private final int size;
    private final HmacKcvType kcvType;
    private final int kcvLength;
    private byte[] value;

    public KeyValueHMAC(final @JsonProperty("size") int size,
                        final @JsonProperty("kcvType") HmacKcvType kcvType,
                        final @JsonProperty("kcvLength") int kcvLength,
                        final @JsonProperty("value") byte[] value) {
        super(UUID.randomUUID());
        if (size < 8 || size > 1024) {
            throw new IllegalArgumentException("Size must be between 8 and 1024");
        }
        Objects.requireNonNull(kcvType, "KcvType must not be null");
        if (kcvLength <= 0) {
            throw new IllegalArgumentException("kcvLength must be greater than 0");
        }
        Objects.requireNonNull(value, "Value must not be null");

        this.size = size;
        this.kcvType = kcvType;
        this.kcvLength = kcvLength;
        this.value = value.clone();
    }

    private KeyValueHMAC(final KeyValueHMAC other) {
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
    public KeyValueHMAC clone() {
        return new KeyValueHMAC(this);
    }

    public int getSize() {
        return size;
    }

    public HmacKcvType getKcvType() {
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
        return "KeyValueHMAC[" +
                "size=" + size + ", " +
                "kcvType=" + kcvType + ", " +
                "kcvLength=" + kcvLength + ", " +
                "value=" + value + ']';
    }

}
