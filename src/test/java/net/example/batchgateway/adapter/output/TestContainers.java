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
    @Container
    private static MinIOContainer minIOContainer = new MinIOContainer("minio/minio:latest");
    @Container
    private static RedisContainer redisContainer = new RedisContainer(RedisContainer.DEFAULT_IMAGE_NAME.withTag(RedisContainer.DEFAULT_TAG));

    @BeforeAll
    @JvmStatic
    public static void beforeAll() {
        mariaDBContainer.start();
        minIOContainer.start();
        redisContainer.start();
    }

    @AfterAll
    @JvmStatic
    public static void afterAll() {
        mariaDBContainer.stop();
        minIOContainer.stop();
        redisContainer.stop();
    }

    @DynamicPropertySource
    @JvmStatic
    public static void registerDynamicProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mariaDBContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mariaDBContainer::getUsername);
        registry.add("spring.datasource.password", mariaDBContainer::getPassword);
        registry.add("minio.url", minIOContainer::getS3URL);
        registry.add("minio.access.key", minIOContainer::getUserName);
        registry.add("minio.access.secret", minIOContainer::getPassword);
        registry.add("spring.data.redis.url", redisContainer::getRedisURI);
    }

    public static MinioClient minioClient() {
        return MinioClient
                .builder()
                .endpoint(minIOContainer.getS3URL())
                .credentials(minIOContainer.getUserName(), minIOContainer.getPassword())
                .build();
    }
}

