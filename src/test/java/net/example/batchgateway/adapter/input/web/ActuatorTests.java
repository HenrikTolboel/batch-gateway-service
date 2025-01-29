package net.example.batchgateway.adapter.input.web;

import net.example.batchgateway.adapter.output.TestContainers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalManagementPort;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"management.server.port=0", "server.port=0"}
)
class ActuatorTests extends TestContainers {

    private EntityExchangeResult<String> get(final URI uri, final String contentType) {
        final var client = WebTestClient.bindToServer().baseUrl(uri.toString()).build();
        final var result = client
                .get()
                .uri(uri)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(contentType)
                .expectBody(String.class)
            .returnResult();

        return result;
    }


    @Test
    void contextLoads() {
    }

    @Value("${springdoc.use-management-port:false}")
    private final Boolean springdocUseManagementPort = false;

    @LocalManagementPort
    int managementPort = 0;
    @LocalServerPort
    int serverPort = 0;

    private String managementUrl() {
        return "http://localhost:" +managementPort;
    }
    private String serverUrl() {
        return "http://localhost:" + serverPort;
    }

    @Test
    @DisplayName("Should return 200 when openAPI")
    @Disabled("TODO: Endpoint moved somewhere not working")
    void test1() throws URISyntaxException {
        final String endpoint;
        if (springdocUseManagementPort) {
            endpoint = managementUrl() + "/actuator/openapi";
        } else {
            endpoint = serverUrl() + "/swagger/v3/api-docs";
        }

        final var result = get(new URI(endpoint), MediaType.APPLICATION_JSON_VALUE);

        assertEquals(HttpStatus.OK, result.getStatus());
        assertTrue(result.getResponseBody().contains("OpenAPI definition"));
    }

    @Test
    @DisplayName("Should return 200 when swagger")
    void test2() throws URISyntaxException {
        final String endpoint;
        if (springdocUseManagementPort) {
            endpoint = managementUrl() + "/actuator/swagger-ui/index.html";
        } else {
            endpoint = serverUrl() + "/swagger-ui/index.html";
        }

        final var result = get(new URI(endpoint), "text/html");

        assertEquals(HttpStatus.OK, result.getStatus());
        assertTrue(result.getResponseBody().contains("<title>Swagger UI</title>"));
    }

    @Test
    @DisplayName("Should return 200 when actuator ")
    void test3() throws URISyntaxException {
        final String endpoint;
        endpoint = managementUrl() + "/actuator/info";

        final var result = get(new URI(endpoint), "application/vnd.spring-boot.actuator.v3+json");

        assertEquals(HttpStatus.OK, result.getStatus());
        assertTrue(result.getResponseBody().contains("BUILD_VERSION"));
    }

    @Test
    @DisplayName("Should return 200 when actuator health-health")
    void test4() throws URISyntaxException {
        final String endpoint;
        endpoint = managementUrl() + "/actuator/health/health";

        final var result = get(new URI(endpoint), "application/vnd.spring-boot.actuator.v3+json");

        assertEquals(HttpStatus.OK, result.getStatus());
        assertTrue(result.getResponseBody().contains("\"status\""));
    }

    @Test
    @DisplayName("Should return 200 when actuator health-readiness")
    void test5() throws URISyntaxException {
        final String endpoint;
        endpoint = managementUrl() + "/actuator/health/readiness";

        final var result = get(new URI(endpoint), "application/vnd.spring-boot.actuator.v3+json");

        assertEquals(HttpStatus.OK, result.getStatus());
        assertTrue(result.getResponseBody().contains("\"status\""));
    }

    @Test
    @DisplayName("Should return 200 when actuator health-liveness")
    void test6() throws URISyntaxException {
        final String endpoint;
        endpoint = managementUrl() + "/actuator/health/liveness";

        final var result = get(new URI(endpoint), "application/vnd.spring-boot.actuator.v3+json");

        assertEquals(HttpStatus.OK, result.getStatus());
        assertTrue(result.getResponseBody().contains("\"status\""));
    }

}
