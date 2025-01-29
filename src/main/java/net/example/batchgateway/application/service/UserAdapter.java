package net.example.batchgateway.application.service;

import net.example.batchgateway.application.domain.model.tenantmodule.TenantId;
import net.example.batchgateway.application.domain.model.usermodule.User;
import net.example.batchgateway.application.domain.model.usermodule.UserId;
import net.example.batchgateway.application.domain.model.events.UserDeletedEvent;
import net.example.batchgateway.application.port.GeneralError;
import net.example.batchgateway.application.port.output.DomainEventRepositoryPort;
import net.example.batchgateway.application.port.output.UserRepositoryPort;
import net.example.utils.dichotomy.Empty;
import net.example.utils.dichotomy.Result;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

class UserAdapter {

    private final UserRepositoryPort userRepository;
    private final DomainEventRepositoryPort domainEventRepository;

    public UserAdapter(final UserRepositoryPort userRepository, final DomainEventRepositoryPort domainEventRepository) {
        this.userRepository = userRepository;
        this.domainEventRepository = domainEventRepository;
    }


    public Result<Optional<User>, GeneralError> findById(final UserId userId) {
        return userRepository.findById(userId);
    }

    public Result<List<User>, GeneralError> findByTenantId(final TenantId tenantId) {
        return userRepository.findByTenantId(tenantId);
    }

    @Transactional
    public Result<User, GeneralError> save(final User user) {
        final Result<User, GeneralError> result = userRepository.save(user);
        if (result.isOK() && domainEventRepository.save(user.domainEvents()).isOK()) {
                user.clearDomainEvents();
            }

        return result;
    }

    @Transactional
    public Result<Empty, GeneralError> delete(final UserId userId) {
        return userRepository.findById(userId)
                .flatMap(optionalUser -> {
                    if (optionalUser.isPresent()) {
                        final Result<Empty, GeneralError> result = userRepository.delete(userId);
                        if (result.isErr()) {
                            return result;
                        } else {
                            final Result<Empty, GeneralError> res = domainEventRepository.save(new UserDeletedEvent(userId));
                            if (res.isErr()) {
                                return res;
                            }
                        }
                    }
                    return Result.ofOK();
                })
                .mapErr(err -> {
                    return err;
                });
    }

}
