package net.example.batchgateway.adapter.output.userrepository;

import net.example.batchgateway.application.domain.model.tenantmodule.TenantId;
import net.example.batchgateway.application.domain.model.usermodule.User;
import net.example.batchgateway.application.domain.model.usermodule.UserId;
import net.example.batchgateway.application.domain.model.usermodule.UserName;
import net.example.batchgateway.application.port.GeneralError;
import net.example.batchgateway.application.port.ServiceError;
import net.example.batchgateway.application.port.output.UserRepositoryPort;
import net.example.utils.dichotomy.Empty;
import net.example.utils.dichotomy.Result;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "storage.service", havingValue = "db", matchIfMissing = false)
class DBUserRepository implements UserRepositoryPort {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public DBUserRepository(final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Result<Optional<User>, GeneralError> findById(final UserId userId) {
        // .... SQL select by userId...
        return Result.ofOK(Optional.empty());
    }

    @Override
    public Result<List<User>, GeneralError> findByTenantId(final TenantId tenantId) {
        final String sql = "SELECT * FROM Users WHERE tenant_id = :tenant_id ;";

        final MapSqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("tenant_id", tenantId.value().toString());

        final List<User> result;
        try {
            result = namedParameterJdbcTemplate.query(
                    sql,
                    namedParameters, (resultSet, i) -> {
                        return toUser(resultSet);
                    });
        }
        catch (Exception except) {
            return Result.ofErr(new ServiceError.DatabaseError(except));
        }
        return Result.ofOK(result);
    }

    private User toUser(final ResultSet resultSet) throws SQLException {
        return User.initExisting(
                new UserId(UUID.fromString(resultSet.getString("id"))),
                new UserName(resultSet.getString("name")),
                new TenantId(UUID.fromString(resultSet.getString("tenantId")))
        );
    }

    @Override
    @Transactional
    public Result<User, GeneralError> save(final User user) {
        // .... SQL save...
        return Result.ofOK(user);
    }

    @Override
    @Transactional
    public Result<Empty, GeneralError> delete(final UserId userId) {
        // .... SQL delete...
        return Result.ofOK();
    }
}
