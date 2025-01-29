package net.example.batchgateway.application.port.output;

import net.example.batchgateway.application.domain.model.tenantmodule.Tenant;
import net.example.batchgateway.application.domain.model.tenantmodule.TenantId;
import net.example.batchgateway.application.port.GeneralError;
import net.example.utils.dichotomy.Result;

import java.util.Optional;

public interface TenantRepositoryPort {
    Result<Optional<Tenant>, GeneralError> findById(final TenantId tenantId);

    Result<Tenant, GeneralError> save(final Tenant tenant);
}
