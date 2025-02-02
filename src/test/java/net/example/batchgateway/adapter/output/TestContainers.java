package net.example.batchgateway.adapter.output;

import com.redis.testcontainers.RedisContainer;
import io.minio.MinioClient;
import kotlin.jvm.JvmStatic;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)  // Hikari pool runs dry when multiple tests are run
@Testcontainers
public class TestContainers {
    @Container
    private static MariaDBContainer mariaDBContainer = new MariaDBContainer<>("mariadb:latest");

    @BeforeAll
    @JvmStatic
    public static void beforeAll() {
        mariaDBContainer.start();
    }

    @AfterAll
    @JvmStatic
    public static void afterAll() {
        mariaDBContainer.stop();
    }

    @DynamicPropertySource
    @JvmStatic
    public static void registerDynamicProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mariaDBContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mariaDBContainer::getUsername);
        registry.add("spring.datasource.password", mariaDBContainer::getPassword);
    }

}

