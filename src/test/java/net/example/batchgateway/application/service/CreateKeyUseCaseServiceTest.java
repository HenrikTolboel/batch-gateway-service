package net.example.batchgateway.application.service;

import io.micrometer.observation.ObservationRegistry;
import net.example.batchgateway.application.domain.model.keymodule.CustomAttributes;
import net.example.batchgateway.application.domain.model.keymodule.KcvType;
import net.example.batchgateway.application.domain.model.keymodule.KcvTypeEnum;
import net.example.batchgateway.application.domain.model.keymodule.KeyMaterialAES;
import net.example.batchgateway.application.domain.model.keymodule.KeyName;
import net.example.batchgateway.application.domain.model.keymodule.KeyRevision;
import net.example.batchgateway.application.domain.model.keymodule.KeyState;
import net.example.batchgateway.application.domain.model.keymodule.KeyType;
import net.example.batchgateway.application.domain.model.keymodule.KeyTypeEnum;
import net.example.batchgateway.application.domain.model.keymodule.KeyValue;
import net.example.batchgateway.application.domain.model.keymodule.KeyValueAES;
import net.example.batchgateway.application.domain.model.keymodule.Key;
import net.example.batchgateway.application.domain.model.tenantmodule.TenantId;
import net.example.batchgateway.application.domain.model.usermodule.UserId;
import net.example.batchgateway.application.port.ReportError;
import net.example.batchgateway.application.port.input.CreateKeyUseCasePort;
import net.example.batchgateway.application.port.input.GenerateKeyCommand;
import net.example.batchgateway.application.port.output.*;
import net.example.utils.dichotomy.Result;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class CreateKeyUseCaseServiceTest {

    private final KeyAdapter keyAdapter = mock(KeyAdapter.class);
    private final TenantAdapter tenantAdapter = mock(TenantAdapter.class);
    private final GenerateKeyMaterialPort generateKeyMaterial = mock(GenerateKeyMaterialPort.class);
    private final ObservationRegistry observationRegistry = mock(ObservationRegistry.class);

    private final CreateKeyUseCasePort createKeyUseCaseService = new CreateKeyUseCaseService(keyAdapter,
            tenantAdapter,
            generateKeyMaterial,
            observationRegistry);

    @Test
    void createGeneratesACorrectKey() {

        // GIVEN
        final KeyMaterialAES keyMaterialDetails = new KeyMaterialAES(192,
                KcvType.create(KcvTypeEnum.CMAC),
                1234);
        final GenerateKeyCommand command = new GenerateKeyCommand(TenantId.generate(),
                UserId.generate(),
                KeyName.create("keyName"),
                KeyType.create(KeyTypeEnum.CQL),
                keyMaterialDetails,
                Instant.now(),
                false,
                false,
                30,
                CustomAttributes.empty());

        final KeyValueAES keyValueAES = new KeyValueAES(keyMaterialDetails.size(),
                keyMaterialDetails.kcvType(),
                keyMaterialDetails.kcvLength(),
                "sjdkljsalkd".getBytes());

        when(generateKeyMaterial.generateKeyMaterial(any()))
                .thenReturn(Result.ofOK(keyValueAES));

        when(keyAdapter.save(any())).thenAnswer(i -> Result.ofOK(i.getArgument(0)));

        final ArgumentCaptor<Key> captor = ArgumentCaptor.forClass(Key.class);
        // WHEN
        final var result = createKeyUseCaseService.create(command);

        // THEN

        assertTrue(result.isOK());
        assertTrue(result.ok().isPresent());
        final Key resultKey = result.ok().get();
        assertAll("Key is correctly created",
                () -> assertEquals(command.tenantId(), resultKey.getTenantId()),
                () -> assertEquals(command.keyName(), resultKey.getName()),
                () -> assertEquals(command.keyType(), resultKey.getKeyType()),
                () -> assertEquals(command.autoActivate(), resultKey.isAutoActivateOnCreate()),
                () -> assertEquals(command.autoRotate(), resultKey.isAutoRotateOnExpire()),
                () -> assertEquals(command.keyLifeCycleDays(), resultKey.getKeyLifecycleDays())
        );

        assertTrue(resultKey.currentKeyValue().isPresent());
        final KeyValue resultKeyValue = resultKey.currentKeyValue().get();
        assertAll ("KeyValue is correctly created",
                () -> assertEquals(command.expire(), resultKeyValue.getExpire()),
                () -> assertEquals(KeyState.pre_active, resultKeyValue.getKeyState()),
                () -> assertEquals(keyValueAES, resultKeyValue.getKeyValueDetails())

        );

        assertTrue(resultKey.currentKeyRevision().isPresent());
        final KeyRevision resultKeyRevision = resultKey.currentKeyRevision().get();
        assertAll("KeyRevision is correctly created",
                () -> assertEquals(resultKeyValue.getId(), resultKeyRevision.getKeyValueId()),
                () -> assertEquals(command.customAttributes(), resultKeyRevision.getCustomAttributes())
        );

        verify(keyAdapter, times(1)).save(captor.capture());
    }

    @Test
    void createFailsWithErrorWhenGenerateFails() {

        // GIVEN
        final KeyMaterialAES keyMaterialDetails = new KeyMaterialAES(192,
                KcvType.create(KcvTypeEnum.CMAC),
                1234);
        final GenerateKeyCommand command = new GenerateKeyCommand(TenantId.generate(),
                UserId.generate(),
                KeyName.create("keyName"),
                KeyType.create(KeyTypeEnum.CQL),
                keyMaterialDetails,
                Instant.now(),
                false,
                false,
                30,
                CustomAttributes.empty());

        final KeyValueAES keyValueAES = new KeyValueAES(keyMaterialDetails.size(),
                keyMaterialDetails.kcvType(),
                keyMaterialDetails.kcvLength(),
                "sjdkljsalkd".getBytes());

        when(generateKeyMaterial.generateKeyMaterial(any()))
                .thenReturn(Result.ofErr(new ReportError.ReportClientUnexpectedError(new RuntimeException())));

        when(keyAdapter.save(any())).thenAnswer(i -> Result.ofOK(i.getArgument(0)));

        final ArgumentCaptor<Key> captor = ArgumentCaptor.forClass(Key.class);
        // WHEN
        final var result = createKeyUseCaseService.create(command);

        // THEN

        assertTrue(result.isErr());

        verify(keyAdapter, times(0)).save(captor.capture());
    }

}
