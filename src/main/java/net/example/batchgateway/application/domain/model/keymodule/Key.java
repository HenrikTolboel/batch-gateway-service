package net.example.batchgateway.application.domain.model.keymodule;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.example.batchgateway.application.domain.model.Aggregate;
import net.example.batchgateway.application.domain.model.tenantmodule.TenantId;
import net.example.batchgateway.application.domain.model.events.KeyCreatedEvent;
import net.example.batchgateway.application.domain.model.events.KeyUpdatedEvent;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

public final class Key extends Aggregate<KeyId> {

    private final TenantId tenantId;
    private KeyName name;
    private boolean enabledForUse;
    private final KeyType keyType;
    private final Instant created;
    private final int keyLifecycleDays;
    private boolean autoRotateOnExpire;
    private boolean autoActivateOnCreate;

    @JsonProperty("keyRevisions")
    private KeyRevisions keyRevisions = new KeyRevisions();

    @JsonProperty("keyValues")
    private KeyValues keyValues = new KeyValues();

    @JsonProperty("currentKeyRevisionId")
    private KeyRevisionId currentKeyRevisionId;


    private Key(final KeyId keyId,
                final TenantId tenantId,
                final KeyName name,
                final boolean enabledForUse,
                final KeyType keyType,
                final Instant created,
                final int keyLifecycleDays,
                final boolean autoRotateOnExpire,
                final boolean autoActivateOnCreate) {
        super(keyId);
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        Objects.requireNonNull(name, "Key name cannot be null");
        Objects.requireNonNull(keyType, "Key type cannot be null");
        Objects.requireNonNull(created, "Key created cannot be null");
        if (keyLifecycleDays < 1) {
            throw new IllegalArgumentException("Key lifecycle days cannot be less than 1");
        }
        this.tenantId = tenantId;
        this.name = name;
        this.enabledForUse = enabledForUse;
        this.keyType = keyType;
        this.created = created;
        this.keyLifecycleDays = keyLifecycleDays;
        this.autoRotateOnExpire = autoRotateOnExpire;
        this.autoActivateOnCreate = autoActivateOnCreate;
    }

    @JsonCreator
    Key(final @JsonProperty("id") KeyId id,
        final @JsonProperty("tenantId") TenantId tenantId,
        final @JsonProperty("name") KeyName name,
        final @JsonProperty("enabledForUse") boolean enabledForUse,
        final @JsonProperty("keyType") KeyType keyType,
        final @JsonProperty("created") Instant created,
        final @JsonProperty("keyLifecycleDays") int keyLifecycleDays,
        final @JsonProperty("autoRotateOnExpire") boolean autoRotateOnExpire,
        final @JsonProperty("autoActivateOnCreate") boolean autoActivateOnCreate,
        final @JsonProperty("currentKeyRevisionId") KeyRevisionId currentKeyRevisionId,
        final @JsonProperty("keyRevisions") KeyRevisions keyRevisions,
        final @JsonProperty("keyValues") KeyValues keyValues) {

        this(id, tenantId, name, enabledForUse, keyType, created, keyLifecycleDays, autoRotateOnExpire, autoActivateOnCreate);
        this.currentKeyRevisionId = currentKeyRevisionId;
        this.keyRevisions = keyRevisions;
        this.keyValues = keyValues;

    }

    public static Key initExisting(final KeyId keyId,
                                   final TenantId tenantId,
                                   final KeyName name,
                                   final boolean enabledForUse,
                                   final KeyType keyType,
                                   final Instant created,
                                   final int keyLifecycleDays,
                                   final boolean autoRotateOnExpire,
                                   final boolean autoActivateOnCreate) {
        return new Key(keyId,
                tenantId,
                name,
                enabledForUse,
                keyType,
                created,
                keyLifecycleDays,
                autoRotateOnExpire,
                autoActivateOnCreate);
    }

    public static Key create(final TenantId tenantId,
                             final KeyName name,
                             final boolean enabledForUse,
                             final KeyType keyType,
                             final int keyLifecycleDays,
                             final boolean autoRotateOnExpire,
                             final boolean autoActivateOnCreate) {
        final Key key = new Key(KeyId.generate(),
                tenantId,
                name,
                enabledForUse,
                keyType,
                Instant.now(),
                keyLifecycleDays,
                autoRotateOnExpire,
                autoActivateOnCreate);
        key.registerEvent(new KeyCreatedEvent(key.getId()));
        return key;
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public KeyName getName() {
        return name;
    }

    public boolean isEnabledForUse() {
        return enabledForUse;
    }

    public KeyType getKeyType() {
        return keyType;
    }

    public Instant getCreated() {
        return created;
    }

    public int getKeyLifecycleDays() {
        return keyLifecycleDays;
    }

    public boolean isAutoRotateOnExpire() {
        return autoRotateOnExpire;
    }

    public boolean isAutoActivateOnCreate() {
        return autoActivateOnCreate;
    }

    public void add(final KeyValue keyValue) {
        Objects.requireNonNull(keyValue, "KeyValue cannot be null");
        if (keyValues.containsKey(keyValue.getId())) {
            throw new IllegalStateException("KeyValue already exist with same id");
        }
        keyValues.add(keyValue);
    }

    public void add(final KeyRevision keyRevision) {
        Objects.requireNonNull(keyRevision, "KeyRevision cannot be null");
        if (keyRevisions.containsKey(keyRevision.getId())) {
            throw new IllegalStateException("KeyRevision already exist with same id");
        }
        if (!keyValues.containsKey(keyRevision.getKeyValueId())) {
            throw new IllegalStateException("KeyRevision contains reference to non existing KeyValue");
        }
        keyRevisions.add(keyRevision);
        currentKeyRevisionId = keyRevision.getId();
    }

    public Optional<KeyRevision> currentKeyRevision() {
        if (hasKeyRevision()) {
            return Optional.of(keyRevisions.get(currentKeyRevisionId).clone());
        }
        return Optional.empty();
    }

    public Optional<KeyValue> currentKeyValue() {
        if (hasKeyRevision()) {
            return Optional.of(keyValues.get(keyRevisions.get(currentKeyRevisionId).getKeyValueId()).clone());
        }
        return Optional.empty();
    }

    private boolean hasKeyRevision() {
        return null != currentKeyRevisionId
                && keyRevisions.containsKey(currentKeyRevisionId)
                && keyValues.containsKey(keyRevisions.get(currentKeyRevisionId).getKeyValueId());
    }

    public boolean activate() {
        return activate(false);
    }

    private boolean activate(final boolean onlyPreActive) {
        if (hasKeyRevision()) {
            final KeyRevision curKeyRevision = keyRevisions.get(currentKeyRevisionId);

            final KeyValue curKeyValue = keyValues.get(curKeyRevision.getKeyValueId());

            if (curKeyValue.getKeyState().equals(KeyState.pre_active)) {
                final AtomicBoolean updated = new AtomicBoolean(false);
                keyValues.forEach((keyValueId, keyValue) -> {
                    if (keyValue.deactivate()) {
                        updated.set(true);
                    }
                });
                if (curKeyValue.activate()) {
                    registerEvent(new KeyUpdatedEvent(getId()));
                    return true;
                }
            } else if (!onlyPreActive && curKeyValue.getKeyState().equals(KeyState.deactivated) && curKeyValue.activate()) {
                registerEvent(new KeyUpdatedEvent(getId()));
                return true;
            }

        }
        return false;
    }

    public boolean deactivate() {
        if (hasKeyRevision()) {
            final KeyRevision curKeyRevision = keyRevisions.get(currentKeyRevisionId);
            final KeyValue curKeyValue = keyValues.get(curKeyRevision.getKeyValueId());
            if (curKeyValue.deactivate()) {
                registerEvent(new KeyUpdatedEvent(getId()));
                return true;
            }
        }
        return false;
    }

    public boolean deactivatePreviousKey() {
        if (hasKeyRevision()) {
            final KeyRevision curKeyRevision = keyRevisions.get(currentKeyRevisionId);

            final KeyValue curKeyValue = keyValues.get(curKeyRevision.getKeyValueId());
            if (curKeyValue.getKeyState().equals(KeyState.pre_active)) {
                final AtomicBoolean updated = new AtomicBoolean(false);
                keyValues.forEach((keyValueId, keyValue) -> {
                    if (keyValue.deactivate()) {
                        updated.set(true);
                    }
                });
                if (updated.get()) {
                    registerEvent(new KeyUpdatedEvent(getId()));
                    return true;
                }
            }
        }
        return false;
    }

    public boolean destroy() {
        return false; // TODO
    }

    public boolean autoActivate() {
        return autoActivateOnCreate && activate(true);
    }

    public boolean reshape(final KeyValue keyValue) {
        Objects.requireNonNull(keyValue, "KeyValue cannot be null");

        if (! hasKeyRevision()) {
            return false;
        }

        final KeyRevision keyRevision = KeyRevision.create(keyValue.getId(),
                currentKeyRevision().get().getCustomAttributes());

        add(keyValue);
        add(keyRevision);

        registerEvent(new KeyUpdatedEvent(getId()));

        return true;
    }

    public void setName(final KeyName name) {
        this.name = name;
        registerEvent(new KeyUpdatedEvent(getId()));
    }

    public void setEnabledForUse(final boolean enabledForUse) {
        this.enabledForUse = enabledForUse;
        registerEvent(new KeyUpdatedEvent(getId()));
    }

    public void setAutoRotateOnExpire(final boolean autoRotateOnExpire) {
        this.autoRotateOnExpire = autoRotateOnExpire;
        registerEvent(new KeyUpdatedEvent(getId()));
    }

    public void setAutoActivateOnCreate(final boolean autoActivateOnCreate) {
        this.autoActivateOnCreate = autoActivateOnCreate;
        registerEvent(new KeyUpdatedEvent(getId()));
    }

    @Override
    public String toString() {
        return "Key[" +
                "Id=" + getId() +
                ", tenantId=" + tenantId +
                ", name='" + name + '\'' +
                ", enabledForUse=" + enabledForUse +
                ", keyType=" + keyType +
                ", created=" + created +
                ", keyLifecycleDays=" + keyLifecycleDays +
                ", autoRotateOnExpire=" + autoRotateOnExpire +
                ", autoActivateOnCreate=" + autoActivateOnCreate +
                ']';
    }

    public Instant earliestActionNeeded() {
        return Instant.now(); // TODO: loop through expire and return earliest
    }


    public void forCurrentRevision(final BiConsumer<? super KeyRevision, ? super KeyValue> action) {
        Objects.requireNonNull(action);
        final Optional<KeyRevision> optionalKeyRevision = this.currentKeyRevision();
        final Optional<KeyValue> optionalKeyValue = this.currentKeyValue();
        if (optionalKeyRevision.isEmpty() || optionalKeyValue.isEmpty()) {
            throw new IllegalStateException("Key has no current key revision");
        }

        action.accept(optionalKeyRevision.get().clone(), optionalKeyValue.get().clone());
    }

    public void forEachRevision(final BiConsumer<? super KeyRevision, ? super KeyValue> action) {
        Objects.requireNonNull(action);

        this.keyRevisions.getKeyRevisionsAsSortedList().forEach(keyRevision -> {
            action.accept(keyRevision.clone(), this.keyValues.get(keyRevision.getKeyValueId()).clone());
        });
    }

}
