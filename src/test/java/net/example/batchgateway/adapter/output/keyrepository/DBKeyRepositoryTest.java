package net.example.batchgateway.adapter.output.keyrepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.example.batchgateway.adapter.output.TestContainers;
import net.example.batchgateway.application.domain.model.keymodule.CustomAttributes;
import net.example.batchgateway.application.domain.model.keymodule.KcvType;
import net.example.batchgateway.application.domain.model.keymodule.KcvTypeEnum;
import net.example.batchgateway.application.domain.model.keymodule.Key;
import net.example.batchgateway.application.domain.model.keymodule.KeyId;
import net.example.batchgateway.application.domain.model.keymodule.KeyName;
import net.example.batchgateway.application.domain.model.keymodule.KeyOrigin;
import net.example.batchgateway.application.domain.model.keymodule.KeyRestrictions;
import net.example.batchgateway.application.domain.model.keymodule.KeyRevision;
import net.example.batchgateway.application.domain.model.keymodule.KeyRevisionId;
import net.example.batchgateway.application.domain.model.keymodule.KeyState;
import net.example.batchgateway.application.domain.model.keymodule.KeyType;
import net.example.batchgateway.application.domain.model.keymodule.KeyTypeEnum;
import net.example.batchgateway.application.domain.model.keymodule.KeyValue;
import net.example.batchgateway.application.domain.model.keymodule.KeyValue3DES;
import net.example.batchgateway.application.domain.model.keymodule.KeyValueAES;
import net.example.batchgateway.application.domain.model.keymodule.KeyValueId;
import net.example.batchgateway.application.domain.model.tenantmodule.TenantId;
import net.example.batchgateway.application.port.GeneralError;
import net.example.batchgateway.application.port.output.KeyRepositoryPort;
import net.example.batchgateway.config.SpringBootObjectMapper;
import net.example.utils.dichotomy.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = { "spring.flyway.enabled=true", "domainevent.publisher.delay=9000000" }
)
@Testcontainers
class DBKeyRepositoryTest extends TestContainers {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final ObjectMapper objectMapper = SpringBootObjectMapper.getObjectMapper();



    private final Key existingKey1 = createKey();

    private Key createKey() {
        final Key key1 = Key.initExisting(KeyId.generate(),
                TenantId.generate(),
                KeyName.create("existing key read"),
                true,
                KeyType.create(KeyTypeEnum.CQL),
                Instant.now(),
                30,
                false,
                false);

        final KeyRestrictions keyRestrictions1 = new KeyRestrictions(123, false);
        final KeyOrigin keyOrigin1 = new KeyOrigin("origin");
        final KeyValueAES keyValueAES = new KeyValueAES(192,
                KcvType.create(KcvTypeEnum.CMAC),
                1234,
                "kslsksælkadælks".getBytes());
        final KeyValue3DES keyValue3DES = new KeyValue3DES(128,
                KcvType.create(KcvTypeEnum.CMAC),
                4321,
                "kslsdsadsdsadsadsksælkadælks".getBytes());

        final KeyValue keyValue1 = KeyValue.initExisting(KeyValueId.generate(),
                KeyState.active,
                Instant.now(),
                Instant.now(),
                keyRestrictions1,
                keyOrigin1,
                keyValueAES);
        final KeyValue keyValue2 = KeyValue.initExisting(KeyValueId.generate(),
                KeyState.active,
                Instant.now(),
                Instant.now(),
                keyRestrictions1,
                keyOrigin1,
                keyValue3DES);

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

        return key1;
    }

    @BeforeEach
    void insertKeys() {
        final KeyRepositoryPort keyRepository = new DBKeyRepository(objectMapper, namedParameterJdbcTemplate);
        keyRepository.save(existingKey1);
    }

    @Test
    void contextLoads() {
    }

    @Test
    void testDBversion() {
        final String actualDatabaseVersion =
                namedParameterJdbcTemplate.getJdbcTemplate().queryForObject("SELECT version()", String.class);

        assertTrue(actualDatabaseVersion.contains("MariaDB"));
    }


    @Test
    void save() {

        final KeyRepositoryPort keyRepository = new DBKeyRepository(objectMapper, namedParameterJdbcTemplate);


        final Key key1 = createKey();


        final Result<Key, GeneralError> res = keyRepository.save(key1);

        assertNotNull(res);
        assertTrue(res.isOK());

        final Result<Optional<Key>, GeneralError> find = keyRepository.findById(key1.getId());

        assertNotNull(find);


    }

    @Test
    void findById() {

        final KeyRepositoryPort keyRepository = new DBKeyRepository(objectMapper, namedParameterJdbcTemplate);

        final var res = keyRepository.findById(existingKey1.getId());

        assert(res.isOK());
        assert(res.expect().isPresent());
    }
}
