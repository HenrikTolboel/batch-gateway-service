package net.example.batchgateway.adapter.input.web;

import net.example.batchgateway.adapter.input.web.dto.GenerateDTO;
import net.example.batchgateway.adapter.input.web.dto.GenerateWithIdDTO;
import net.example.batchgateway.adapter.input.web.dto.KcvTypeDTO;
import net.example.batchgateway.adapter.input.web.dto.KeyMaterial3DESDTO;
import net.example.batchgateway.adapter.input.web.dto.KeyMaterialAESDTO;
import net.example.batchgateway.adapter.input.web.dto.KeyTypeDTO;
import net.example.batchgateway.adapter.input.web.dto.ListRevisionsDTO;
import net.example.batchgateway.application.domain.model.keymodule.*;
import net.example.batchgateway.application.domain.model.tenantmodule.TenantId;
import net.example.batchgateway.application.port.ServiceError;
import net.example.batchgateway.application.port.input.CreateKeyUseCasePort;
import net.example.batchgateway.application.port.input.GenerateKeyCommand;
import net.example.batchgateway.application.port.input.GenerateKeyWithIdCommand;
import net.example.batchgateway.application.port.input.KeyManagementUseCasePort;
import net.example.batchgateway.application.port.input.QueryKeyUseCasePort;
import net.example.batchgateway.config.CreateJwtToken;
import net.example.batchgateway.config.SpringBootObjectMapper;
import net.example.utils.dichotomy.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class KeysControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private QueryKeyUseCasePort queryKeyUseCase;

    @MockitoBean
    private CreateKeyUseCasePort createKeyUseCase;

    @MockitoBean
    private KeyManagementUseCasePort keyManagementUseCase;

    @LocalServerPort
    private int serverPort;

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
            KeyState.deactivated,
            Instant.now(),
            Instant.now(),
            keyRestrictions1,
            keyOrigin1,
            keyValue3DES);

    private String token = CreateJwtToken.oAuthToken("Alice", Arrays.asList("Role1", "Role2"));

    @Test
    void contextLoads() {
    }

    @Test
    void listRevisions_success() {

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


        when(queryKeyUseCase.findKeyById(any()))
                .thenReturn(Result.ofOK(Optional.of(key1)));

        final EntityExchangeResult<String> result = webTestClient
                .get()
                .uri("/keys/" + key1.getId().value().toString() + "/listRevisions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
                .expectBody(String.class)
                .returnResult();

        assertNotNull(result);

        final ListRevisionsDTO list = SpringBootObjectMapper.readValue(result.getResponseBody(), ListRevisionsDTO.class);

        assertNotNull(list);



    }
    @Test
    void listRevisions_key_not_found_returns_problemdetail() {

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


        when(queryKeyUseCase.findKeyById(any()))
                .thenReturn(Result.ofOK(Optional.empty()));

        final var result = webTestClient
                .get()
                .uri("/keys/" + key1.getId().value().toString() + "/listRevisions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .expectBody(String.class)
                .returnResult();

        assertNotNull(result);

        final ProblemDetail pd = SpringBootObjectMapper.readValue(result.getResponseBody(), ProblemDetail.class);

        assertNotNull(pd);

    }

    @Test
    void listRevisions_FindKey_returns_error_returns_problemdetail() {

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


        when(queryKeyUseCase.findKeyById(any()))
                .thenReturn(Result.ofErr(new ServiceError.DatabaseErrorMessage("database failed")));

        final var result = webTestClient
                .get()
                .uri("/keys/" + key1.getId().value().toString() + "/listRevisions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .expectBody(String.class)
                .returnResult();

        assertNotNull(result);

        final ProblemDetail pd = SpringBootObjectMapper.readValue(result.getResponseBody(), ProblemDetail.class);

        assertNotNull(pd);

    }

    @Test
    void generate_success_3DES() {

        var generateDTO = new GenerateDTO(
                "keyname",
                KeyTypeDTO.create("CQL"),
                new KeyMaterial3DESDTO(192, KcvTypeDTO.create("CMAC"), 1234),
                Instant.now().plusSeconds(100000),
                true,
                true,
                30,
                new HashMap<>());

        when(createKeyUseCase.create(any(GenerateKeyCommand.class)))
                .thenReturn(Result.ofOK(key1));

        final EntityExchangeResult<Void> result = webTestClient
                .post()
                .uri("/keys")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(generateDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody().isEmpty();

        assertNotNull(result);
        assertEquals(key1.getId().value().toString(), Objects.requireNonNull(result.getResponseHeaders().get("Location")).getFirst());
    }

    @Test
    void generate_fails_3DES() {

        var generateDTO = new GenerateDTO(
                "keyname",
                KeyTypeDTO.create("CQL"),
                new KeyMaterial3DESDTO(192, KcvTypeDTO.create("CMAC"), 1234),
                Instant.now().plusSeconds(100000),
                true,
                true,
                30,
                new HashMap<>());

        when(createKeyUseCase.create(any(GenerateKeyCommand.class)))
                .thenReturn(Result.ofErr(new ServiceError.DatabaseErrorMessage("database failed")));

        final EntityExchangeResult<ProblemDetail> result = webTestClient
                .post()
                .uri("/keys")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(generateDTO)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .expectBody(ProblemDetail.class)
                .returnResult();

        assertNotNull(result);
        assertEquals(503, Objects.requireNonNull(result.getResponseBody()).getStatus());
    }

    @Test
    void generate_success_3DESWithId() {

        var generateDTO = new GenerateWithIdDTO(
                UUID.randomUUID().toString(),
                "keyname",
                KeyTypeDTO.create("CQL"),
                new KeyMaterial3DESDTO(192, KcvTypeDTO.create("CMAC"), 1234),
                Instant.now().plusSeconds(100000),
                true,
                true,
                30,
                new HashMap<>());

        when(createKeyUseCase.create(any(GenerateKeyWithIdCommand.class)))
                .thenReturn(Result.ofOK(key1));

        final EntityExchangeResult<Void> result = webTestClient
                .post()
                .uri("/keys/withid")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(generateDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody().isEmpty();

        assertNotNull(result);
        assertEquals(key1.getId().value().toString(), Objects.requireNonNull(result.getResponseHeaders().get("Location")).getFirst());
    }



    @Test
    void generate_success_AES() {

        var generateDTO = new GenerateDTO(
                "keyname",
                KeyTypeDTO.create("CQL"),
                new KeyMaterialAESDTO(192, KcvTypeDTO.create("CMAC"), 1234),
                Instant.now().plusSeconds(100000),
                true,
                true,
                30,
                new HashMap<>());

        when(createKeyUseCase.create(any(GenerateKeyCommand.class)))
                .thenReturn(Result.ofOK(key1));

        final EntityExchangeResult<Void> result = webTestClient
                .post()
                .uri("/keys")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(generateDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody().isEmpty();

        assertNotNull(result);
        assertEquals(key1.getId().value().toString(), Objects.requireNonNull(result.getResponseHeaders().get("Location")).getFirst());
    }

    @Test
    void generate_fails_AES() {

        GenerateDTO generateDTO = new GenerateDTO(
                "keyname",
                KeyTypeDTO.create("CQL"),
                new KeyMaterialAESDTO(192, KcvTypeDTO.create("CMAC"), 1234),
                Instant.now().plusSeconds(100000),
                true,
                true,
                30,
                new HashMap<>());

        when(createKeyUseCase.create(any(GenerateKeyCommand.class)))
                .thenReturn(Result.ofErr(new ServiceError.DatabaseErrorMessage("database failed")));

        final EntityExchangeResult<ProblemDetail> result = webTestClient
                .post()
                .uri("/keys")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(generateDTO)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .expectBody(ProblemDetail.class)
                .returnResult();

        assertNotNull(result);
        assertEquals(503, Objects.requireNonNull(result.getResponseBody()).getStatus());
    }

    @Test
    void activate_success() {

        when(keyManagementUseCase.activateKey(any())).thenReturn(Result.ofOK());

        final EntityExchangeResult<Void> result = webTestClient
                .put()
                .uri("/keys/" + UUID.randomUUID().toString() + "/activate")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        assertNotNull(result);


    }

    @Test
    void activate_fails() {


        when(keyManagementUseCase.activateKey(any())).thenReturn(Result.ofErr(new ServiceError.DatabaseErrorMessage("database failed")));

        final EntityExchangeResult<ProblemDetail> result = webTestClient
                .put()
                .uri("/keys/" + UUID.randomUUID().toString() + "/activate")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ProblemDetail.class)
                .returnResult();

        assertNotNull(result);


    }



    @Test
    void deactivate_success() {


        when(keyManagementUseCase.deactivateKey(any())).thenReturn(Result.ofOK());

        final EntityExchangeResult<Void> result = webTestClient
                .put()
                .uri("/keys/" + UUID.randomUUID().toString() + "/deactivate")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        assertNotNull(result);


    }

    @Test
    void deactivate_fails() {


        when(keyManagementUseCase.deactivateKey(any())).thenReturn(Result.ofErr(new ServiceError.DatabaseErrorMessage("database failed")));

        final EntityExchangeResult<ProblemDetail> result = webTestClient
                .put()
                .uri("/keys/" + UUID.randomUUID().toString() + "/deactivate")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ProblemDetail.class)
                .returnResult();

        assertNotNull(result);


    }

    @Test
    void deactivateprevious_success() {


        when(keyManagementUseCase.deactivatePreviousKey(any())).thenReturn(Result.ofOK());

        final EntityExchangeResult<Void> result = webTestClient
                .put()
                .uri("/keys/" + UUID.randomUUID().toString() + "/deactivateprevious")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        assertNotNull(result);


    }

    @Test
    void deactivateprevious_fails() {


        when(keyManagementUseCase.deactivatePreviousKey(any())).thenReturn(Result.ofErr(new ServiceError.DatabaseErrorMessage("database failed")));

        final EntityExchangeResult<ProblemDetail> result = webTestClient
                .put()
                .uri("/keys/" + UUID.randomUUID().toString() + "/deactivateprevious")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ProblemDetail.class)
                .returnResult();

        assertNotNull(result);


    }

}
