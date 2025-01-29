package net.example.batchgateway.application.service;

import io.micrometer.observation.ObservationRegistry;
import net.example.batchgateway.application.domain.model.keymodule.CustomAttributes;
import net.example.batchgateway.application.domain.model.keymodule.KcvType;
import net.example.batchgateway.application.domain.model.keymodule.KcvTypeEnum;
import net.example.batchgateway.application.domain.model.keymodule.Key;
import net.example.batchgateway.application.domain.model.keymodule.KeyId;
import net.example.batchgateway.application.domain.model.keymodule.KeyMaterialAES;
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
import net.example.batchgateway.application.domain.model.tenantmodule.Tenant;
import net.example.batchgateway.application.domain.model.tenantmodule.TenantName;
import net.example.batchgateway.application.domain.model.usermodule.UserId;
import net.example.batchgateway.application.port.DomainError;
import net.example.batchgateway.application.port.input.ReshapeKeyByGenerateCommand;
import net.example.batchgateway.application.port.output.GenerateKeyMaterialPort;
import net.example.utils.dichotomy.Result;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReshapeKeyUseCaseServiceTest {

    private final KeyAdapter keyAdapter = mock(KeyAdapter.class);
    private final TenantAdapter tenantAdapter = mock(TenantAdapter.class);
    private final GenerateKeyMaterialPort generateKeyMaterial = mock(GenerateKeyMaterialPort.class);
    private final ObservationRegistry observationRegistry = mock(ObservationRegistry.class);

    private final ReshapeKeyUseCaseService reshapeKeyUseCaseService =
            new ReshapeKeyUseCaseService(keyAdapter, tenantAdapter, generateKeyMaterial, observationRegistry);

    private final Tenant tenant = Tenant.create(new TenantName("myTenant"), true);

    private KeyValue createKeyValue(final KeyState keyState) {
        return KeyValue.initExisting(KeyValueId.generate(),
                keyState,
                Instant.now(),
                Instant.now(),
                new KeyRestrictions(123, true),
                new KeyOrigin("my origin"),
                new KeyValue3DES(128, KcvType.create(KcvTypeEnum.CMAC), 1212, "isdosioidosod".getBytes())
        );
    }

    private KeyRevision createKeyRevision(final KeyValue keyValue) {
        return KeyRevision.initExisting(KeyRevisionId.generate(),
                Instant.now(),
                keyValue.getId(),
                CustomAttributes.empty()
        );
    }

    private Key createKey() {
        return Key.initExisting(KeyId.generate(),
                tenant.getId(),
                KeyName.create("asdsad"),
                true,
                KeyType.create(KeyTypeEnum.CQL),
                Instant.now(),
                30,
                true,
                true
        );
    }

    private Key addRevisionAndValueToKey(final Key key, final KeyRevision keyRevision, final KeyValue keyValue) {
        key.add(keyValue);
        key.add(keyRevision);
        return key;
    }


    @Test
    void reshapeSuccess() {

        final KeyValue keyValue = createKeyValue(KeyState.active);
        final KeyRevision keyRevision = createKeyRevision(keyValue);
        final Key key = addRevisionAndValueToKey(createKey(), keyRevision, keyValue);

        when(generateKeyMaterial.generateKeyMaterial(any())).thenReturn(Result.ofOK(new KeyValueAES(192,
                KcvType.create(KcvTypeEnum.CMAC),
                12345,
                "kljdkljdklajklskldjakls".getBytes())));
        when(keyAdapter.findById(key.getId())).thenReturn(Result.ofOK(Optional.of(key)));
        when(keyAdapter.save(key)).thenReturn(Result.ofOK(key));

        final var result = reshapeKeyUseCaseService.reshape(new ReshapeKeyByGenerateCommand(tenant.getId(),
                UserId.generate(),
                key.getId(),
                new KeyMaterialAES(192,
                        KcvType.create(KcvTypeEnum.CMAC),
                        1234), Instant.now()));

        assertTrue(result.isOK());

        verify(keyAdapter, times(1)).findById(any());
        verify(generateKeyMaterial, times(1)).generateKeyMaterial(any());
        verify(keyAdapter, times(1)).save(any());

    }

    @Test
    void reshapeKeyNotExist() {

        final KeyValue keyValue = createKeyValue(KeyState.active);
        final KeyRevision keyRevision = createKeyRevision(keyValue);
        final Key key = addRevisionAndValueToKey(createKey(), keyRevision, keyValue);

        when(generateKeyMaterial.generateKeyMaterial(any())).thenReturn(Result.ofOK(new KeyValueAES(192,
                KcvType.create(KcvTypeEnum.CMAC),
                12345,
                "kljdkljdklajklskldjakls".getBytes())));
        when(keyAdapter.findById(key.getId())).thenReturn(Result.ofOK(Optional.empty()));
        when(keyAdapter.save(key)).thenReturn(Result.ofOK(key));

        final var result = reshapeKeyUseCaseService.reshape(new ReshapeKeyByGenerateCommand(tenant.getId(),
                UserId.generate(),
                key.getId(),
                new KeyMaterialAES(192,
                        KcvType.create(KcvTypeEnum.CMAC),
                        1234), Instant.now()));

        assertTrue(result.isOK());

        verify(keyAdapter, times(1)).findById(any());
        verify(generateKeyMaterial, times(0)).generateKeyMaterial(any());
        verify(keyAdapter, times(0)).save(any());

    }

    @Test
    void reshapeGenerateFails() {

        final KeyValue keyValue = createKeyValue(KeyState.active);
        final KeyRevision keyRevision = createKeyRevision(keyValue);
        final Key key = addRevisionAndValueToKey(createKey(), keyRevision, keyValue);

        when(generateKeyMaterial.generateKeyMaterial(any())).thenReturn(Result.ofErr(new DomainError.KeyReshapeError(key.getId())));
        when(keyAdapter.findById(key.getId())).thenReturn(Result.ofOK(Optional.of(key)));
        when(keyAdapter.save(key)).thenReturn(Result.ofOK(key));

        final var result = reshapeKeyUseCaseService.reshape(new ReshapeKeyByGenerateCommand(tenant.getId(),
                UserId.generate(),
                key.getId(),
                new KeyMaterialAES(192,
                        KcvType.create(KcvTypeEnum.CMAC),
                        1234), Instant.now()));

        assertTrue(result.isErr());

        verify(keyAdapter, times(1)).findById(any());
        verify(generateKeyMaterial, times(1)).generateKeyMaterial(any());
        verify(keyAdapter, times(0)).save(any());

    }
}
