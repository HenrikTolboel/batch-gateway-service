package net.example.batchgateway.application.domain.model.keymodule;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.example.batchgateway.application.domain.model.Entity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public final class KeyValue extends Entity<KeyValueId> {

    private KeyState keyState;
    private final Instant created;
    private final Instant expire;
    private final KeyRestrictions keyRestrictions;
    private final KeyOrigin origin;
    private final KeyValueDetails keyValueDetails;

    private KeyValue(final KeyValueId keyValueId,
                    final KeyState keyState,
                    final Instant created,
                    final Instant expire,
                    final KeyRestrictions keyRestrictions,
                    final KeyOrigin origin,
                    final KeyValueDetails keyValueDetails
    ) {
        super(keyValueId);
        Objects.requireNonNull(keyState, "keyState must not be null");
        Objects.requireNonNull(created, "created must not be null");
        Objects.requireNonNull(expire, "expire must not be null");
        Objects.requireNonNull(keyRestrictions, "keyRestrictions must not be null");
        Objects.requireNonNull(origin, "origin must not be null");
        Objects.requireNonNull(keyValueDetails, "keyValueDetails must not be null");
        this.keyState = keyState;
        this.created = created;
        this.expire = expire;
        this.keyRestrictions = keyRestrictions;
        this.origin = origin;
        this.keyValueDetails = keyValueDetails;
    }

    @JsonCreator
    public static KeyValue initExisting(final @JsonProperty("id") KeyValueId keyValueId,
                                        final @JsonProperty("keyState") KeyState keyState,
                                        final @JsonProperty("created") Instant created,
                                        final @JsonProperty("expire") Instant expire,
                                        final @JsonProperty("keyRestrictions") KeyRestrictions keyRestrictions,
                                        final @JsonProperty("origin") KeyOrigin origin,
                                        final @JsonProperty("keyValueDetails") KeyValueDetails keyValueDetails) {
        return new KeyValue(keyValueId, keyState, created, expire, keyRestrictions, origin, keyValueDetails);
    }

    public static KeyValue create(final int keyLifecycleDays,
                                  final KeyRestrictions keyRestrictions,
                                  final KeyOrigin origin,
                                  final KeyValueDetails keyValueDetails) {
        final Instant now = Instant.now();
        return new KeyValue(KeyValueId.generate(),
                new KeyState(KeyStateEnum.PREACTIVE),
                now,
                now.plus(keyLifecycleDays, ChronoUnit.DAYS),
                keyRestrictions,
                origin,
                keyValueDetails);
    }

    public static KeyValue create(final Instant expire,
                                  final KeyRestrictions keyRestrictions,
                                  final KeyOrigin origin,
                                  final KeyValueDetails keyValueDetails) {
        final Instant now = Instant.now();
        return new KeyValue(KeyValueId.generate(),
                new KeyState(KeyStateEnum.PREACTIVE),
                now,
                expire,
                keyRestrictions,
                origin,
                keyValueDetails);
    }

    protected KeyValue clone() {
        return new KeyValue(getId(), keyState, created, expire, keyRestrictions, origin, keyValueDetails.clone());
    }

    protected boolean activate() {
        if (getKeyState().equals(KeyState.pre_active)
                || getKeyState().equals(KeyState.deactivated)) {
            keyState = KeyState.active;
            return true;
        }
        return false;
    }

    protected boolean deactivate() {
        if (getKeyState().equals(KeyState.active)) {
            keyState = KeyState.deactivated;
            return true;
        }
        return false;
    }

    protected boolean destroy() {
        if (!getKeyState().equals(KeyState.destroyed)) {
            keyState = KeyState.destroyed;
            keyValueDetails.destroyValue();
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "KeyValue[" +
                "Id=" + getId() +
                ", keyState=" + keyState +
                ", created=" + created +
                ", expire=" + expire +
                ", keyRestrictions=" + keyRestrictions +
                ", origin=" + origin +
                ", keyValueDetails=" + keyValueDetails +
                ']';
    }

    public KeyState getKeyState() {
        return keyState;
    }

    public Instant getCreated() {
        return created;
    }

    public Instant getExpire() {
        return expire;
    }

    public KeyRestrictions getKeyRestrictions() {
        return keyRestrictions;
    }

    public KeyOrigin getOrigin() {
        return origin;
    }

    public KeyValueDetails getKeyValueDetails() {
        return keyValueDetails.clone();
    }
}
