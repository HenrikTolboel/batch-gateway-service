package net.example.batchgateway.adapter.output.tenantrepository;

import net.example.batchgateway.application.domain.model.tenantmodule.Tenant;
import net.example.batchgateway.application.domain.model.tenantmodule.TenantId;
import net.example.batchgateway.application.domain.model.events.TenantCreatedEvent;
import net.example.batchgateway.application.domain.model.events.TenantUpdatedEvent;
import net.example.batchgateway.application.port.GeneralError;
import net.example.batchgateway.application.port.output.TenantRepositoryPort;
import net.example.utils.dichotomy.Result;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
class MemoryTenantRepository implements TenantRepositoryPort {

    private final Map<TenantId, Tenant> tenants = new HashMap<>();

    public MemoryTenantRepository() {
    }

    @Override
    public Result<Optional<Tenant>, GeneralError> findById(final TenantId tenantId) {
        final Optional<Tenant> result;
        if (tenants.containsKey(tenantId)) {
            result = Optional.of(tenants.get(tenantId));
        } else {
            result = Optional.empty();
        }
        return Result.ofOK(result);
    }

    @Override
    @Transactional
    public Result<Tenant, GeneralError> save(final Tenant tenant) {
        final boolean existingTenant = tenants.containsKey(tenant.getId());
        tenants.put(tenant.getId(), tenant);
        if (existingTenant) {
            tenant.registerEvent(new TenantUpdatedEvent(tenant.getId()));
        } else {
            tenant.registerEvent(new TenantCreatedEvent(tenant.getId()));
        }

        return Result.ofOK(tenant);
    }
}
