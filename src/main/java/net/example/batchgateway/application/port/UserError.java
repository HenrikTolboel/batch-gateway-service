package net.example.batchgateway.application.port;

import net.example.batchgateway.application.domain.model.usermodule.UserId;

public sealed interface UserError extends GeneralError {
    record UserNotFoundError(UserId userId) implements UserError { }
}
