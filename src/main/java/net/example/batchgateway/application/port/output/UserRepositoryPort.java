package net.example.batchgateway.application.port.output;

import net.example.batchgateway.application.domain.model.tenantmodule.TenantId;
import net.example.batchgateway.application.domain.model.usermodule.User;
import net.example.batchgateway.application.domain.model.usermodule.UserId;
import net.example.batchgateway.application.port.GeneralError;
import net.example.utils.dichotomy.Empty;
import net.example.utils.dichotomy.Result;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
    Result<Optional<User>, GeneralError> findById(final UserId userId);

    Result<List<User>, GeneralError> findByTenantId(final TenantId tenantId);

    Result<User, GeneralError> save(final User user);

    Result<Empty, GeneralError> delete(final UserId userId);
}
