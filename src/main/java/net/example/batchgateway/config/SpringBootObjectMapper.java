package net.example.batchgateway.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class SpringBootObjectMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringBootObjectMapper.class);

    private static ObjectMapper mapper = getObjectMapper();

    @Bean
    @Primary
    public ObjectMapper objectMapper() {

        return getObjectMapper();
    }

    public static ObjectMapper getObjectMapper() {

        if (mapper != null) {
            return mapper;
        }

        mapper = new ObjectMapper();

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mapper.registerModule(new JavaTimeModule());

        return mapper;
    }

    public static <T> T readValue(final String content, final Class<T> valueType) {
        try {
            return getObjectMapper().readValue(content, valueType);
        } catch (JsonProcessingException e) {
            LOGGER.error("read json value failed: ", e);
            return null;
        }
    }

    public static String writeValueAsString(final Object value) {
        try {
            return getObjectMapper().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            LOGGER.error("write json value failed: ", e);
            return "";
        }
    }

    public static boolean isValidJSON(final String json) {
        try {
            getObjectMapper().readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }

}
