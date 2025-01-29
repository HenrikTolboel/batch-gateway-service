package net.example.batchgateway.application.domain.model.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.Instant;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "eventType")
abstract public class Event {
    private final String id;
    private final Long timestamp;
    private final Instant instant;

    @JsonCreator
    protected Event(final @JsonProperty("id") String id, @JsonProperty("timestamp") final Long timestamp, @JsonProperty("instant") final Instant instant) {
        this.id = id;
        this.timestamp = timestamp;
        this.instant = instant;
    }

    public Event() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
        this.instant = Instant.now();
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getId() {
        return id;
    }

    public Instant getInstant() {
        return instant;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[eventId=" + id + ", instant=" + instant + "]";
    }

}
