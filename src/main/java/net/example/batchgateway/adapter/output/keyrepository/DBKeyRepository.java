package net.example.batchgateway.adapter.output.keyrepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.example.batchgateway.application.domain.model.keymodule.Key;
import net.example.batchgateway.application.domain.model.keymodule.KeyId;
import net.example.batchgateway.application.port.GeneralError;
import net.example.batchgateway.application.port.ServiceError;
import net.example.batchgateway.application.port.output.KeyRepositoryPort;
import net.example.utils.dichotomy.Result;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
@ConditionalOnProperty(name = "storage.service", havingValue = "db", matchIfMissing = false)
class DBKeyRepository implements KeyRepositoryPort {

    private final ObjectMapper objectMapper;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public DBKeyRepository(final ObjectMapper objectMapper,
                           final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Result<Key, GeneralError> save(final Key key) {

        final String json;
        try {
            json = objectMapper.writeValueAsString(key);

        } catch (JsonProcessingException e) {
            return Result.ofErr(new ServiceError.DatabaseError(e));
        }

        //TODO: UPDATE when exist
        final String updateSql = "UPDATE KeysTable set attributes = :attributes, earliest_action_needed_seconds = :earliest_action_needed_seconds WHERE key_id = :key_id ;";

        final String sql = "INSERT INTO KeysTable (key_id, earliest_action_needed_seconds, attributes) values(:key_id, :earliest_action_needed_seconds, :attributes) ;";

        final MapSqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("key_id", key.getId().value().toString())
                .addValue("earliest_action_needed_seconds", key.earliestActionNeeded().getEpochSecond())
                .addValue("attributes", json);

        try {
            int rows = namedParameterJdbcTemplate.update(updateSql, namedParameters);
            if (rows == 0) {
                rows = namedParameterJdbcTemplate.update(sql, namedParameters);
            }

        } catch (DataAccessException dataAccessException) {
            return Result.ofErr(new ServiceError.DatabaseError(dataAccessException));
        }

        return Result.ofOK(key);
    }


    @Override
    public Result<Optional<Key>, GeneralError> findById(final KeyId keyId) {
        final String sql = "SELECT * FROM KeysTable WHERE key_id = :key_id ;";

        final MapSqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("key_id", keyId.value().toString());

        final List<Key> result;
        try {
            result = namedParameterJdbcTemplate.query(
                    sql,
                    namedParameters,
                    (resultSet, i) -> {
                        return toKey(resultSet);
                    });
        } catch (DataAccessException dataAccessException) {
            return Result.ofErr(new ServiceError.DatabaseError(dataAccessException));
        }

        if (result.isEmpty()) {
            return Result.ofOK(Optional.empty());
        }

        return Result.ofOK(Optional.of(result.getFirst()));
    }

    private Key toKey(final ResultSet resultSet)  {

        final Key key;
        try {
            final String keyId = resultSet.getString("key_id");
            final String attributes = resultSet.getString("attributes");
            key = objectMapper.readValue(attributes, Key.class);
        } catch (JsonProcessingException | SQLException e) {
            throw new RuntimeException(e);
        }

        return key;
    }

}
