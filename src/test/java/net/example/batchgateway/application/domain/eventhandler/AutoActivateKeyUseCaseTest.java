package net.example.batchgateway.application.domain.eventhandler;

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
import net.example.batchgateway.application.domain.model.keymodule.KeyValueAES;
import net.example.batchgateway.application.domain.model.keymodule.KeyValueId;
import net.example.batchgateway.application.domain.model.tenantmodule.TenantId;
import net.example.batchgateway.application.domain.model.events.KeyCreatedEvent;
import net.example.batchgateway.application.port.output.KeyRepositoryPort;
import net.example.utils.dichotomy.Result;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AutoActivateKeyUseCaseTest {
    private final KeyRepositoryPort keyRepository = mock(KeyRepositoryPort.class);
    private final AutoActivateKeyUseCase autoActivateKeyUseCase = new AutoActivateKeyUseCase(keyRepository);


    private Key initKey(final boolean autoActivateOnCreate) {
        return Key.initExisting(KeyId.generate(),
                TenantId.generate(),
                KeyName.create("existing key read"),
                true,
                KeyType.create(KeyTypeEnum.CQL),
                Instant.now(),
                30,
                false,
                autoActivateOnCreate);
    }

    private KeyValue initKeyValue(final KeyState keyState) {
        final KeyRestrictions keyRestrictions1 = new KeyRestrictions(123, false);
        final KeyOrigin keyOrigin1 = new KeyOrigin("origin");
        final KeyValueAES keyValueAES = new KeyValueAES(192,
                KcvType.create(KcvTypeEnum.CMAC),
                1234,
                "kslsksælkadælks".getBytes());

        return KeyValue.initExisting(KeyValueId.generate(),
                keyState,
                Instant.now(),
                Instant.now(),
                keyRestrictions1,
                keyOrigin1,
                keyValueAES);
    }

    private KeyRevision initKeyRevision(final KeyValueId keyValueId) {
        return KeyRevision.initExisting(KeyRevisionId.generate(), Instant.now(), keyValueId, CustomAttributes.empty());
    }

    @Test
    void onKeyCreatedEventKeyWithoutRevisionDontCallSave() {
        // GIVEN
        final Key key = initKey(true);

        final KeyCreatedEvent keyCreatedEvent = new KeyCreatedEvent(key.getId());

        when(keyRepository.findById(key.getId())).thenReturn(Result.ofOK(Optional.of(key)));
        when(keyRepository.save(key)).thenReturn(Result.ofOK(key));

        final ArgumentCaptor<Key> captor = ArgumentCaptor.forClass(Key.class);

        // WHEN
        autoActivateKeyUseCase.onKeyCreatedEvent(keyCreatedEvent);

        // THEN
        verify(keyRepository, times(0)).save(captor.capture());
    }

    @Test
    void onKeyCreatedEventKeyWithPreactiveValueCallsSaveAndGeneratesEvent() {
        // GIVEN
        final Key key = initKey(true);
        final KeyValue keyValue = initKeyValue(KeyState.pre_active);
        final KeyRevision keyRevision = initKeyRevision(keyValue.getId());
        key.add(keyValue);
        key.add(keyRevision);

        final KeyCreatedEvent keyCreatedEvent = new KeyCreatedEvent(key.getId());

        when(keyRepository.findById(key.getId())).thenReturn(Result.ofOK(Optional.of(key)));
        when(keyRepository.save(key)).thenReturn(Result.ofOK(key));

        final ArgumentCaptor<Key> captor = ArgumentCaptor.forClass(Key.class);

        assertEquals(KeyState.pre_active, key.currentKeyValue().get().getKeyState());
        assertTrue(key.domainEvents().isEmpty());

        // WHEN
        autoActivateKeyUseCase.onKeyCreatedEvent(keyCreatedEvent);

        // THEN
        verify(keyRepository, times(1)).save(captor.capture());

        final Key captorKey = captor.getValue();
        assertEquals(KeyState.active, captorKey.currentKeyValue().get().getKeyState());
        assertEquals(1, captorKey.domainEvents().size());
    }

    @Test
    void onKeyCreatedEventKeyWithActiveValueDoNotCallSave() {
        // GIVEN
        final Key key = initKey(true);
        final KeyValue keyValue = initKeyValue(KeyState.active);
        final KeyRevision keyRevision = initKeyRevision(keyValue.getId());
        key.add(keyValue);
        key.add(keyRevision);

        final var keyCreatedEvent = new KeyCreatedEvent(key.getId());

        when(keyRepository.findById(key.getId())).thenReturn(Result.ofOK(Optional.of(key)));
        when(keyRepository.save(key)).thenReturn(Result.ofOK(key));

        final ArgumentCaptor<Key> captor = ArgumentCaptor.forClass(Key.class);

        assertEquals(KeyState.active, key.currentKeyValue().get().getKeyState());
        assertTrue(key.domainEvents().isEmpty());

        // WHEN
        autoActivateKeyUseCase.onKeyCreatedEvent(keyCreatedEvent);

        // THEN
        verify(keyRepository, times(0)).save(captor.capture());
    }
}
