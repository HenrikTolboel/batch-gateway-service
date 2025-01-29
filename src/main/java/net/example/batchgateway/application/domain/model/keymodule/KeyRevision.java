package net.example.batchgateway.application.domain.model.keymodule;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.example.batchgateway.application.domain.model.Entity;

import java.time.Instant;
import java.util.Objects;

public final class KeyRevision extends Entity<KeyRevisionId> {
    private final Instant created;
    private final KeyValueId keyValueId;

    @JsonProperty("customAttributes")
    private final CustomAttributes customAttributes;

    private KeyRevision(final KeyRevisionId keyRevisionId,
                        final Instant created,
                        final KeyValueId keyValueId,
                        final CustomAttributes customAttributes) {
        super(keyRevisionId);
        Objects.requireNonNull(created, "created is null");
        Objects.requireNonNull(keyValueId, "keyValueId is null");
        Objects.requireNonNull(customAttributes, "CustomAttributes is null");
        this.created = created;
        this.keyValueId = keyValueId;
        this.customAttributes = customAttributes;
    }

    @JsonCreator
    public static KeyRevision initExisting(final @JsonProperty("id") KeyRevisionId keyRevisionId,
                                           final @JsonProperty("created") Instant created,
                                           final @JsonProperty("keyValueId") KeyValueId keyValueId,
                                           final @JsonProperty("customAttributes") CustomAttributes customAttributes) {
        return new KeyRevision(keyRevisionId, created, keyValueId, customAttributes);
    }

    public static KeyRevision create(final KeyValueId keyValueId, final CustomAttributes customAttributes) {
        return new KeyRevision(KeyRevisionId.generate(), Instant.now(), keyValueId, customAttributes);
    }

    public Instant getCreated() {
        return created;
    }

    protected KeyRevision clone() {
        return new KeyRevision(getId(), created, keyValueId, customAttributes.clone());
    }

    @Override
    public String toString() {
        return "KeyRevision[" +
                "Id=" + getId() +
                ", created=" + created +
                ", keyValueId=" + keyValueId +
                ']';
    }

    public KeyValueId getKeyValueId() {
        return keyValueId;
    }

    public CustomAttributes getCustomAttributes() {return customAttributes.clone();}
}
