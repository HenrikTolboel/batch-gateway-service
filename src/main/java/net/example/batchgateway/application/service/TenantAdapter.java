package net.example.batchgateway.application.service;

import net.example.batchgateway.application.domain.model.tenantmodule.Tenant;
import net.example.batchgateway.application.domain.model.tenantmodule.TenantId;
import net.example.batchgateway.application.port.GeneralError;
import net.example.batchgateway.application.port.output.DomainEventRepositoryPort;
import net.example.batchgateway.application.port.output.TenantRepositoryPort;
import net.example.utils.dichotomy.Result;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
class TenantAdapter {

    private final TenantRepositoryPort tenantRepository;
    private final DomainEventRepositoryPort domainEventRepository;

    public TenantAdapter(final TenantRepositoryPort tenantRepository, final DomainEventRepositoryPort domainEventRepository) {
        this.tenantRepository = tenantRepository;
        this.domainEventRepository = domainEventRepository;
    }

    public Result<Optional<Tenant>, GeneralError> findById(final TenantId tenantId) {
        return tenantRepository.findById(tenantId);
    }

    @Transactional
    public Result<Tenant, GeneralError> save(final Tenant tenant) {
        final Result<Tenant, GeneralError> result = tenantRepository.save(tenant);
        if (result.isOK() && domainEventRepository.save(tenant.domainEvents()).isOK()) {
                tenant.clearDomainEvents();
            }

        return result;
    }

}
