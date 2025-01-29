package net.example.batchgateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

@Configuration
public class CertificateConfiguration {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Environment environment;
    private final WebClient.Builder webClientBuilder;


    @Value("${CONFIG_SERVICE_URL:fakeUrl}")
    private String configServiceUrl;
    @Value("${CONFIG_SERVICE_USERNAME:fakeUsername}")
    private String configServiceUsername;
    @Value("${CONFIG_SERVICE_PASSWORD:fakePassword}")
    private String configServicePassword;
    @Value("${spring.application.name}")
    private String appName;
    @Value("classpath:AppleRootCA-G3.cer")
    private Resource certResource;

    public CertificateConfiguration(Environment environment, WebClient.Builder webClientBuilder) {
        this.environment = environment;
        this.webClientBuilder = webClientBuilder;
    }

    @Bean
    File getAppleRootCertificate() throws IOException {
        if ("fakeUrl".equals(configServiceUrl) ||
                "fakeUsername".equals(configServiceUsername) ||
                "fakePassword".equals(configServicePassword)) {
            logger.info("Config Service credentials invalid. Using local copy of certificate.");
            return certResource.getFile();
        }

        Flux<DataBuffer> certFlux = webClientBuilder
                .baseUrl(configServiceUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(basicAuthentication(configServiceUsername, configServicePassword))
                .build()
                .get()
                .uri(String.format("/%s/%s/main/certificates/AppleRootCA-G3.cer", appName, environment.getDefaultProfiles()[0]))
                .header("Accept", "application/octet-stream")
                .retrieve()
                .bodyToFlux(DataBuffer.class);
        var certPath = System.getProperty("java.io.tmpdir") + "/AppleRootCA-G3.cer";
        DataBufferUtils.write(certFlux, Paths.get(certPath), StandardOpenOption.CREATE).block();
        return new File(certPath);
    }
}
