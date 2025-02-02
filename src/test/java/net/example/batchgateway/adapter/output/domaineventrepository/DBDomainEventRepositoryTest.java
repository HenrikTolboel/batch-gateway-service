package net.example.batchgateway.adapter.output.domaineventrepository;

import net.example.batchgateway.adapter.output.TestContainers;
import net.example.batchgateway.application.domain.model.events.DomainEvent;
import net.example.batchgateway.application.domain.model.usermodule.UserId;
import net.example.batchgateway.application.domain.model.events.TestEvent;
import net.example.batchgateway.application.port.GeneralError;
import net.example.batchgateway.application.port.output.DomainEventRepositoryPort;
import net.example.batchgateway.config.SpringBootObjectMapper;
import net.example.utils.dichotomy.Empty;
import net.example.utils.dichotomy.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = { "spring.flyway.enabled=true", "domainevent.publisher.delay=9000000" }
)
@Testcontainers
class DBDomainEventRepositoryTest extends TestContainers {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void testDBversion() {
        final String actualDatabaseVersion =
                namedParameterJdbcTemplate.getJdbcTemplate().queryForObject("SELECT version()", String.class);

        assertTrue(actualDatabaseVersion.contains("MariaDB"));
    }

    @Test
    void testDomainEventRepository() {

        final DomainEventRepositoryPort repository = new DBDomainEventRepository(SpringBootObjectMapper.getObjectMapper(), namedParameterJdbcTemplate);

        final TestEvent event = new TestEvent(UserId.generate());

        final Result<Empty, GeneralError> resSave = repository.save(event);

        final Result<List<DomainEvent>, GeneralError> res10 = repository.findTop10DomainEvents();

        assertTrue(resSave.isOK());
        assertTrue(res10.isOK());
        assertEquals(1, res10.expect().size());
        assertEquals(event.getDomainEventId(), res10.expect().getFirst().getDomainEventId());
        assertEquals(event.getTimestamp(), res10.expect().getFirst().getTimestamp());
        assertEquals(event.getUserId(), ((TestEvent)res10.expect().getFirst()).getUserId());
    }
}
