package net.example.batchgateway.adapter.output;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = { "spring.flyway.enabled=true", "domainevent.publisher.delay=9000000" }
)
@Testcontainers
class DBTest extends TestContainers {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void testDBversion() {
        final String actualDatabaseVersion =
                namedParameterJdbcTemplate.getJdbcTemplate().queryForObject("SELECT version()", String.class);

        assertTrue(actualDatabaseVersion.contains("MariaDB"));
    }
}
