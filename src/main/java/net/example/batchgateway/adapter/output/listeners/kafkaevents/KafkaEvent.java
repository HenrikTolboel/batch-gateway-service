package net.example.batchgateway.adapter.output.listeners.kafkaevents;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.UUID;

enum KafkaEvents {
    USER_ADDED,
    USER_UPDATED,
    USER_DELETED
}

@JsonInclude(JsonInclude.Include.NON_NULL)
public record KafkaEvent(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        Instant event_time,
        KafkaEvents event_name,
        UUID event_id,
        Object payload) {
}
