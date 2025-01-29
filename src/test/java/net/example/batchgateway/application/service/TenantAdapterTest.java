package net.example.batchgateway.application.service;

import net.example.batchgateway.application.domain.model.tenantmodule.Tenant;
import net.example.batchgateway.application.domain.model.tenantmodule.TenantName;
import net.example.batchgateway.application.port.GeneralError;
import net.example.batchgateway.application.port.ServiceError;
import net.example.batchgateway.application.port.output.DomainEventRepositoryPort;
import net.example.batchgateway.application.port.output.TenantRepositoryPort;
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

class TenantAdapterTest {
    private final TenantRepositoryPort tenantRepository = mock(TenantRepositoryPort.class);
    private final DomainEventRepositoryPort domainEventRepository = mock(DomainEventRepositoryPort.class);

    private final TenantAdapter tenantAdapter = new TenantAdapter(tenantRepository, domainEventRepository);

    @Test
    void saveSuccesSavingAndSavingEvents() {
        final Tenant tenant = Tenant.create(new TenantName("tenantName"),true);

        assertFalse(tenant.domainEvents().isEmpty());

        when(tenantRepository.save(tenant)).thenReturn(Result.ofOK(tenant));

        when(domainEventRepository.save(tenant.domainEvents())).thenReturn(Result.ofOK());

        final Result<Tenant, GeneralError> result = tenantAdapter.save(tenant);


        assertNotNull(result);
        assertTrue(result.isOK());
        assertEquals(tenant, result.expect());

        assertTrue(tenant.domainEvents().isEmpty());

        verify(tenantRepository, times(1)).save(any());
        verify(domainEventRepository, times(1)).save(anyList());

    }

    @Test
    void saveFailSavingDontSendEvents() {
        final Tenant tenant = Tenant.create(new TenantName("tenantName"),true);

        assertFalse(tenant.domainEvents().isEmpty());

        when(tenantRepository.save(tenant)).thenReturn(Result.ofErr(new ServiceError.DatabaseError(new Throwable())));

        when(domainEventRepository.save(tenant.domainEvents())).thenReturn(Result.ofOK());

        final Result<Tenant, GeneralError> result = tenantAdapter.save(tenant);


        assertNotNull(result);
        assertTrue(result.isErr());

        assertFalse(tenant.domainEvents().isEmpty());

        verify(tenantRepository, times(1)).save(any());
        verify(domainEventRepository, times(0)).save(anyList());

    }
    
}
