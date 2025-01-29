package net.example.batchgateway.application.service;

import io.micrometer.observation.ObservationRegistry;
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
import net.example.batchgateway.application.domain.model.keymodule.KeyValueId;
import net.example.batchgateway.application.domain.model.tenantmodule.Tenant;
import net.example.batchgateway.application.domain.model.tenantmodule.TenantName;
import net.example.batchgateway.application.domain.model.usermodule.UserId;
import net.example.batchgateway.application.port.input.ActivateKeyCommand;
import net.example.batchgateway.application.port.input.DeactivateKeyCommand;
import net.example.batchgateway.application.port.input.DeactivatePreviousKeyCommand;
import net.example.utils.dichotomy.Result;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class KeyManagementUseCaseServiceTest {

    private final KeyAdapter keyAdapter = mock(KeyAdapter.class);
    private final TenantAdapter tenantAdapter = mock(TenantAdapter.class);
    private final ObservationRegistry observationRegistry = mock(ObservationRegistry.class);

    private final KeyManagementUseCaseService keyManagementUseCaseService =
            new KeyManagementUseCaseService(keyAdapter, tenantAdapter, observationRegistry);

    private final Tenant tenant = Tenant.create(new TenantName("myTenant"), true);

    private KeyValue createKeyValue(KeyState keyState) {
        return KeyValue.initExisting(KeyValueId.generate(),
                keyState,
                Instant.now(),
                Instant.now(),
                new KeyRestrictions(123, true),
                new KeyOrigin("my origin"),
                new KeyValue3DES(128, KcvType.create(KcvTypeEnum.CMAC), 1212, "isdosioidosod".getBytes())
        );
    }

    private KeyRevision createKeyRevision(KeyValue keyValue) {
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

    private Key addRevisionAndValueToKey(Key key, KeyRevision keyRevision, KeyValue keyValue) {
        key.add(keyValue);
        key.add(keyRevision);
        return key;
    }

    @Test
    void activateKeySuccess() {
        final KeyValue keyValue = createKeyValue(KeyState.pre_active);
        final KeyRevision keyRevision = createKeyRevision(keyValue);
        final Key key = addRevisionAndValueToKey(createKey(), keyRevision, keyValue);

        when(keyAdapter.findById(key.getId())).thenReturn(Result.ofOK(Optional.of(key)));
        when(keyAdapter.save(key)).thenReturn(Result.ofOK(key));

        var result = keyManagementUseCaseService.activateKey(new ActivateKeyCommand(tenant.getId(), UserId.generate(), key.getId()));

        assertTrue(result.isOK());

        verify(keyAdapter, times(1)).findById(any());
        verify(keyAdapter, times(1)).save(any());
    }

    @Test
    void activateKeySaveOnlyCalledWhenStateChanges() {
        final KeyValue keyValue = createKeyValue(KeyState.active);
        final KeyRevision keyRevision = createKeyRevision(keyValue);
        final Key key = addRevisionAndValueToKey(createKey(), keyRevision, keyValue);

        when(keyAdapter.findById(key.getId())).thenReturn(Result.ofOK(Optional.of(key)));
        when(keyAdapter.save(key)).thenReturn(Result.ofOK(key));

        var result = keyManagementUseCaseService.activateKey(new ActivateKeyCommand(tenant.getId(), UserId.generate(), key.getId()));

        assertTrue(result.isOK());

        verify(keyAdapter, times(1)).findById(any());
        verify(keyAdapter, times(0)).save(any());
    }

    @Test
    void deactivateKeySuccess() {
        final KeyValue keyValue = createKeyValue(KeyState.active);
        final KeyRevision keyRevision = createKeyRevision(keyValue);
        final Key key = addRevisionAndValueToKey(createKey(), keyRevision, keyValue);

        when(keyAdapter.findById(key.getId())).thenReturn(Result.ofOK(Optional.of(key)));
        when(keyAdapter.save(key)).thenReturn(Result.ofOK(key));

        var result = keyManagementUseCaseService.deactivateKey(new DeactivateKeyCommand(tenant.getId(), UserId.generate(), key.getId()));

        assertTrue(result.isOK());

        verify(keyAdapter, times(1)).findById(any());
        verify(keyAdapter, times(1)).save(any());
    }

    @Test
    void deactivateKeySaveOnlyCalledWhenStateChanges() {
        final KeyValue keyValue = createKeyValue(KeyState.deactivated);
        final KeyRevision keyRevision = createKeyRevision(keyValue);
        final Key key = addRevisionAndValueToKey(createKey(), keyRevision, keyValue);

        when(keyAdapter.findById(key.getId())).thenReturn(Result.ofOK(Optional.of(key)));
        when(keyAdapter.save(key)).thenReturn(Result.ofOK(key));

        var result = keyManagementUseCaseService.deactivateKey(new DeactivateKeyCommand(tenant.getId(), UserId.generate(), key.getId()));

        assertTrue(result.isOK());

        verify(keyAdapter, times(1)).findById(any());
        verify(keyAdapter, times(0)).save(any());
    }
    
    @Test
    void deactivatePreviousKeyWhenNoPrevoiusKeyDoesnothing() {
        final KeyValue keyValue = createKeyValue(KeyState.active);
        final KeyRevision keyRevision = createKeyRevision(keyValue);
        final Key key = addRevisionAndValueToKey(createKey(), keyRevision, keyValue);

        when(keyAdapter.findById(key.getId())).thenReturn(Result.ofOK(Optional.of(key)));
        when(keyAdapter.save(key)).thenReturn(Result.ofOK(key));

        var result = keyManagementUseCaseService.deactivatePreviousKey(new DeactivatePreviousKeyCommand(tenant.getId(), UserId.generate(), key.getId()));

        assertTrue(result.isOK());

        verify(keyAdapter, times(1)).findById(any());
        verify(keyAdapter, times(0)).save(any());
    }

    @Test
    void deactivatePreviousKeySaveOnlyCalledWhenStateChanges() {
        final KeyValue keyValue = createKeyValue(KeyState.deactivated);
        final KeyRevision keyRevision = createKeyRevision(keyValue);
        final Key key = addRevisionAndValueToKey(createKey(), keyRevision, keyValue);

        when(keyAdapter.findById(key.getId())).thenReturn(Result.ofOK(Optional.of(key)));
        when(keyAdapter.save(key)).thenReturn(Result.ofOK(key));

        var result = keyManagementUseCaseService.deactivatePreviousKey(new DeactivatePreviousKeyCommand(tenant.getId(), UserId.generate(), key.getId()));

        assertTrue(result.isOK());

        verify(keyAdapter, times(1)).findById(any());
        verify(keyAdapter, times(0)).save(any());
    }

    @Test
    void deactivatePreviousKeySavesWhenaPreviousKeyCanBeDeactivated() {
        final KeyValue keyValue1 = createKeyValue(KeyState.deactivated);
        final KeyRevision keyRevision1 = createKeyRevision(keyValue1);
        Key key = addRevisionAndValueToKey(createKey(), keyRevision1, keyValue1);

        final KeyValue keyValue2 = createKeyValue(KeyState.active);
        final KeyRevision keyRevision2 = createKeyRevision(keyValue2);
        key = addRevisionAndValueToKey(key, keyRevision2, keyValue2);

        final KeyValue keyValue3 = createKeyValue(KeyState.pre_active);
        final KeyRevision keyRevision3 = createKeyRevision(keyValue3);
        key = addRevisionAndValueToKey(key, keyRevision3, keyValue3);

        when(keyAdapter.findById(key.getId())).thenReturn(Result.ofOK(Optional.of(key)));
        when(keyAdapter.save(key)).thenReturn(Result.ofOK(key));
        final ArgumentCaptor<Key> captor = ArgumentCaptor.forClass(Key.class);

        var result = keyManagementUseCaseService.deactivatePreviousKey(new DeactivatePreviousKeyCommand(tenant.getId(), UserId.generate(), key.getId()));

        assertTrue(result.isOK());

        verify(keyAdapter, times(1)).findById(any());
        verify(keyAdapter, times(1)).save(captor.capture());
        final Key captorKey = captor.getValue();

    }

}
