package net.example.batchgateway.application.domain.model.keymodule;

import net.example.batchgateway.application.domain.model.tenantmodule.TenantId;
import net.example.batchgateway.config.SpringBootObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KeyTest {

    private final Key key1 = Key.initExisting(KeyId.generate(),
            TenantId.generate(),
            KeyName.create("existing key read"),
            true,
            KeyType.create(KeyTypeEnum.CQL),
            Instant.now(),
            30,
            false,
            false);

    private final KeyRestrictions keyRestrictions1 = new KeyRestrictions(123, false);
    private final KeyOrigin keyOrigin1 = new KeyOrigin("origin");
    private final KeyValueAES keyValueAES = new KeyValueAES(192,
            KcvType.create(KcvTypeEnum.CMAC),
            1234,
            "kslsksælkadælks".getBytes());
    private final KeyValue3DES keyValue3DES = new KeyValue3DES(128,
            KcvType.create(KcvTypeEnum.CMAC),
            4321,
            "kslsdsadsdsadsadsksælkadælks".getBytes());

    private final KeyValue keyValue1 = KeyValue.initExisting(KeyValueId.generate(),
            KeyState.active,
            Instant.now(),
            Instant.now(),
            keyRestrictions1,
            keyOrigin1,
            keyValueAES);
    private final KeyValue keyValue2 = KeyValue.initExisting(KeyValueId.generate(),
            KeyState.active,
            Instant.now(),
            Instant.now(),
            keyRestrictions1,
            keyOrigin1,
            keyValue3DES);

    @Test
    void addKeyValueThrowsOnNULL() {
        assertThrows(NullPointerException.class, () -> key1.add((KeyValue) null));
    }

    @Test
    void addKeyValueThrowsOnDuplicateKey() {
        key1.add(keyValue1);
        assertThrows(IllegalStateException.class, () -> key1.add(keyValue1));
    }

    @Test
    void addKeyRevisionThrowsOnNULL() {
        assertThrows(NullPointerException.class, () -> key1.add((KeyRevision) null));
    }

    @Test
    void addKeyRevisionThrowsWhenKeyValueNotExist() {
        assertThrows(IllegalStateException.class, () -> key1.add(KeyRevision.create(KeyValueId.generate(), CustomAttributes.empty())));
    }

    @Test
    void addKeyRevisionThrowsWhenKeyRevisionWithSameIdExist() {
        final KeyRevision keyRevision = KeyRevision.initExisting(KeyRevisionId.generate(), Instant.now(), keyValue1.getId(), CustomAttributes.empty());
        key1.add(keyValue1);
        key1.add(keyRevision);
        assertThrows(IllegalStateException.class, () -> key1.add(keyRevision));
    }

    @Test
    void currentKeyRevision() {
    }

    @Test
    void initExistingWhenReadingFromPersistanceDoNotGenerateEvent() {
       final Key key = Key.initExisting(KeyId.generate(),
                TenantId.generate(),
               KeyName.create("existing key read"),
                true,
               KeyType.create(KeyTypeEnum.CQL),
                Instant.now(),
                30,
                false,
                false);

        assertNotNull(key);
        assertEquals(KeyName.create("existing key read"), key.getName());
        assertEquals(0, key.domainEvents().size());
    }

    @Test
    void createWhenWantingNewKeyDoGenerateEvent() {
        final Key key = Key.create(TenantId.generate(),
                KeyName.create("new keys name"),
                true,
                KeyType.create(KeyTypeEnum.CQL),
                30,
                false,
                false);

        assertNotNull(key);
        assertEquals(KeyName.create("new keys name"), key.getName());
        assertEquals(1, key.domainEvents().size());
    }


    @Test
    void serializeKeyAndThenDeserializeToGetSame() {

        final Map<String, Object> customMap = new HashMap<>();
        customMap.put("key1", "value1");
        customMap.put("key2", "value2");

        final CustomAttributes customAttributes = new CustomAttributes(customMap);

        KeyRevision keyRevision = KeyRevision.initExisting(KeyRevisionId.generate(), Instant.now(), keyValue2.getId(), customAttributes);
        key1.add(keyValue2);
        key1.add(keyRevision);
        keyRevision = KeyRevision.initExisting(KeyRevisionId.generate(), Instant.now(), keyValue1.getId(), customAttributes);
        key1.add(keyValue1);
        key1.add(keyRevision);
        keyRevision = KeyRevision.initExisting(KeyRevisionId.generate(), Instant.now(), keyValue1.getId(), customAttributes);
        key1.add(keyRevision);

        final String jsonKey = SpringBootObjectMapper.writeValueAsString(key1);

        assertTrue(jsonKey.length() > 10);


        final Key result = SpringBootObjectMapper.readValue(jsonKey, Key.class);

        assertNotNull(result);

        assertEquals(key1, result);
        assertEquals(key1.currentKeyValue(), result.currentKeyValue());
        assertEquals(key1.currentKeyRevision(), result.currentKeyRevision());
    }

    @Test
    void reshape() {

        final Map<String, Object> customMap = new HashMap<>();
        customMap.put("key1", "value1");
        customMap.put("key2", "value2");

        final CustomAttributes customAttributes = new CustomAttributes(customMap);

        final KeyRevision keyRevision = KeyRevision.initExisting(KeyRevisionId.generate(), Instant.now(), keyValue2.getId(), customAttributes);
        key1.add(keyValue2);
        key1.add(keyRevision);


        final var keyRevisionOldCurrent = key1.currentKeyRevision();
        final var keyValueOldCurrent = key1.currentKeyValue();

        final var result = key1.reshape(keyValue1);

        assertTrue(result);

        final var keyRevisionCurrent = key1.currentKeyRevision();
        final var keyValueCurrent = key1.currentKeyValue();

        assert(true);
    }

    private record aPair(KeyRevision keyrevision, KeyValue keyValue) {}

    @Test
    void forCurrentRevision() {

        final Map<String, Object> customMap = new HashMap<>();
        customMap.put("key1", "value1");
        customMap.put("key2", "value2");

        final CustomAttributes customAttributes = new CustomAttributes(customMap);

        KeyRevision keyRevision = KeyRevision.initExisting(KeyRevisionId.generate(), Instant.now(), keyValue2.getId(), customAttributes);
        key1.add(keyValue2);
        key1.add(keyRevision);
        keyRevision = KeyRevision.initExisting(KeyRevisionId.generate(), Instant.now(), keyValue1.getId(), customAttributes);
        key1.add(keyValue1);
        key1.add(keyRevision);
        keyRevision = KeyRevision.initExisting(KeyRevisionId.generate(), Instant.now(), keyValue1.getId(), customAttributes);
        key1.add(keyRevision);


        final var keyRevisionOldCurrent = key1.currentKeyRevision();
        final var keyValueOldCurrent = key1.currentKeyValue();

        final List<aPair> list = new ArrayList<>();

        key1.forCurrentRevision((keyRev, keyVal) -> {
            list.add(new aPair(keyRev, keyVal));
        });

        assert(true);

    }

    @Test
    void forEachRevision() {
        final Map<String, Object> customMap = new HashMap<>();
        customMap.put("key1", "value1");
        customMap.put("key2", "value2");

        final CustomAttributes customAttributes = new CustomAttributes(customMap);

        KeyRevision keyRevision = KeyRevision.initExisting(KeyRevisionId.generate(), Instant.now(), keyValue2.getId(), customAttributes);
        key1.add(keyValue2);
        key1.add(keyRevision);
        keyRevision = KeyRevision.initExisting(KeyRevisionId.generate(), Instant.now(), keyValue1.getId(), customAttributes);
        key1.add(keyValue1);
        key1.add(keyRevision);
        keyRevision = KeyRevision.initExisting(KeyRevisionId.generate(), Instant.now(), keyValue1.getId(), customAttributes);
        key1.add(keyRevision);


        final var keyRevisionOldCurrent = key1.currentKeyRevision();
        final var keyValueOldCurrent = key1.currentKeyValue();

        final List<aPair> list = new ArrayList<>();

        key1.forEachRevision((keyRev, keyVal) -> {
            list.add(new aPair(keyRev, keyVal));
        });

        assert(true);

    }
}
