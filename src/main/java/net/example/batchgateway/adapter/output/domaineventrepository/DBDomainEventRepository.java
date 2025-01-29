package net.example.batchgateway.adapter.output.domaineventrepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.example.batchgateway.application.domain.model.events.DomainEvent;
import net.example.batchgateway.application.domain.model.events.DomainEventId;
import net.example.batchgateway.application.port.GeneralError;
import net.example.batchgateway.application.port.ServiceError;
import net.example.batchgateway.application.port.output.DomainEventRepositoryPort;
import net.example.utils.dichotomy.Empty;
import net.example.utils.dichotomy.Result;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

@Component
@ConditionalOnProperty(name = "storage.service", havingValue = "db", matchIfMissing = false)
class DBDomainEventRepository implements DomainEventRepositoryPort {

    private final ObjectMapper objectMapper;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public DBDomainEventRepository(final ObjectMapper objectMapper, final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Result<Empty, GeneralError> save(final DomainEvent domainEvent) {


        final long seconds = domainEvent.getTimestamp().getEpochSecond() ;
        final long nanos = domainEvent.getTimestamp().getNano() ;

        final String json;
        try {
            json = objectMapper.writeValueAsString(domainEvent);

        } catch (JsonProcessingException e) {
            return Result.ofErr(new ServiceError.DatabaseError(e));
        }

        final String sql = "INSERT INTO DomainEvents (event_id, seconds, nanos, attributes) values(:event_id, :seconds, :nanos, :attributes) ;";

        final MapSqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("event_id", domainEvent.getDomainEventId().value().toString())
                .addValue("seconds", seconds)
                .addValue("nanos", nanos)
                .addValue("attributes", json);

        namedParameterJdbcTemplate.update(sql, namedParameters);

        return Result.ofOK();
    }

    @Override
    public Result<Empty, GeneralError> save(final List<DomainEvent> domainEvents) {
        final Iterator<DomainEvent> it =domainEvents.iterator();

        Result<Empty, GeneralError> result = Result.ofOK();
        while (it.hasNext()) {
            final Result<Empty, GeneralError> res = save(it.next());
            if (res.isErr() && result.isOK()) {
                result = Result.ofErr(res.err().get());
            }
        }

        return result;
    }

    private boolean delete(final DomainEventId domainEventId) {
        final String sql = "DELETE FROM DomainEvents where event_id = :event_id ;";

        final MapSqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("event_id", domainEventId.value().toString());

        namedParameterJdbcTemplate.update(sql, namedParameters);

        return true;
    }

    @Override
    public Result<Empty, GeneralError> delete(final List<DomainEvent> domainEvents) {
        final Iterator<DomainEvent> it =domainEvents.iterator();

        Result<Empty, GeneralError> result = Result.ofOK();
        while (it.hasNext()) {
            final DomainEventId eventId = it.next().getDomainEventId();
            final boolean res = delete(eventId);
            if (!res && result.isOK()) {
                result = Result.ofErr(new ServiceError.DatabaseErrorMessage("Delete of DomainEvent with id=" + eventId.value().toString()));
            }
        }

        return result;
    }

    @Override
    public Result<List<DomainEvent>, GeneralError> findTop10DomainEvents() {
        final String sql = "SELECT * FROM DomainEvents ORDER BY seconds, nanos ASC LIMIT 10 ;";

        final MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        final List<DomainEvent> result;
        try {
            result = namedParameterJdbcTemplate.query(
                    sql,
                    namedParameters,
                    (resultSet, i) -> {
                        return toDomainEvent(resultSet);
                    });
        }
        catch (Exception except) {
            return Result.ofErr(new ServiceError.DatabaseError(except));
        }

//        Instant instant = Instant.ofEpochSecond( seconds , nanos ) ;

        return Result.ofOK(result);
    }

    private DomainEvent toDomainEvent(final ResultSet resultSet) throws SQLException {
        final String domainEventId = resultSet.getString("event_id");
        final long seconds = resultSet.getLong("seconds");
        final long nanos = resultSet.getLong("nanos");
        final String attributes = resultSet.getString("attributes");

        final DomainEvent domainEvent;
        try {
            domainEvent = objectMapper.readValue(attributes, DomainEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return domainEvent;
    }

}
