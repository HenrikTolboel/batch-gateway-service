package net.example.batchgateway.application.service;

import net.example.batchgateway.application.domain.model.tenantmodule.TenantId;
import net.example.batchgateway.application.domain.model.keymodule.Key;
import net.example.batchgateway.application.domain.model.keymodule.KeyName;
import net.example.batchgateway.application.domain.model.keymodule.KeyType;
import net.example.batchgateway.application.domain.model.keymodule.KeyTypeEnum;
import net.example.batchgateway.application.port.GeneralError;
import net.example.batchgateway.application.port.ServiceError;
import net.example.batchgateway.application.port.output.DomainEventRepositoryPort;
import net.example.batchgateway.application.port.output.KeyRepositoryPort;
import net.example.utils.dichotomy.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class KeyAdapterTest {
    private final KeyRepositoryPort keyRepository = mock(KeyRepositoryPort.class);
    private final DomainEventRepositoryPort domainEventRepository = mock(DomainEventRepositoryPort.class);

    private final KeyAdapter keyAdapter = new KeyAdapter(keyRepository, domainEventRepository);

    @Test
    void saveSuccesSavingAndSavingEvents() {
        final Key key = Key.create(TenantId.generate(),
                KeyName.create("keyName"),
                true,
                KeyType.create(KeyTypeEnum.CQL),
                30,
                true,
                true);

        assertFalse(key.domainEvents().isEmpty());

        when(keyRepository.save(key)).thenReturn(Result.ofOK(key));

        when(domainEventRepository.save(key.domainEvents())).thenReturn(Result.ofOK());

        final Result<Key, GeneralError> result = keyAdapter.save(key);


        assertNotNull(result);
        assertTrue(result.isOK());
        assertEquals(key, result.expect());

        assertTrue(key.domainEvents().isEmpty());

        verify(keyRepository, times(1)).save(any());
        verify(domainEventRepository, times(1)).save(anyList());

    }

    @Test
    void saveFailSavingDontSendEvents() {
        final Key key = Key.create(TenantId.generate(),
                KeyName.create("keyName"),
                true,
                KeyType.create(KeyTypeEnum.CQL),
                30,
                true,
                true);

        assertFalse(key.domainEvents().isEmpty());

        when(keyRepository.save(key)).thenReturn(Result.ofErr(new ServiceError.DatabaseError(new Throwable())));

        when(domainEventRepository.save(key.domainEvents())).thenReturn(Result.ofOK());

        final Result<Key, GeneralError> result = keyAdapter.save(key);


        assertNotNull(result);
        assertTrue(result.isErr());

        assertFalse(key.domainEvents().isEmpty());

        verify(keyRepository, times(1)).save(any());
        verify(domainEventRepository, times(0)).save(anyList());

    }
    
}
