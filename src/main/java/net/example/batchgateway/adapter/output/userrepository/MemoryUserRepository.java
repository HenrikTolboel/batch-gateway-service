package net.example.batchgateway.adapter.output.userrepository;

import net.example.batchgateway.application.domain.model.tenantmodule.TenantId;
import net.example.batchgateway.application.domain.model.usermodule.User;
import net.example.batchgateway.application.domain.model.usermodule.UserId;
import net.example.batchgateway.application.port.GeneralError;
import net.example.batchgateway.application.port.output.UserRepositoryPort;
import net.example.utils.dichotomy.Empty;
import net.example.utils.dichotomy.Result;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@ConditionalOnProperty(name = "storage.service", havingValue = "memory", matchIfMissing = true)
class MemoryUserRepository implements UserRepositoryPort {

    private final Map<UserId, User> users = new HashMap<>();

    @Override
    public Result<Optional<User>, GeneralError> findById(final UserId userId) {
        return null;
    }

    @Override
    public Result<List<User>, GeneralError> findByTenantId(final TenantId tenantId) {
        return null;
    }

    @Override
    @Transactional
    public Result<User, GeneralError> save(final User user) {
        users.put(user.getId(), user);

        return Result.ofOK(user);
    }

    @Override
    @Transactional
    public Result<Empty, GeneralError> delete(final UserId userId) {
        users.remove(userId);

        return Result.ofOK();
    }
}
