package net.example.batchgateway.application.domain.model.keymodule;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

public class KeyValues {


    @JsonProperty("keyValues")
    private final Map<String, KeyValue> keyValues;

    public KeyValues() {
        this.keyValues = new HashMap<>();
    }

    public void add(final KeyValue keyValue) {
        keyValues.put(keyValue.getId().asString(), keyValue);
    }

    public KeyValue get(final KeyValueId keyValueId) {
        return keyValues.get(keyValueId.asString());
    }

    public boolean containsKey(final KeyValueId keyValueId) {
        return keyValues.containsKey(keyValueId.asString());
    }

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KeyValues that)) {
            return false;
        }

        return keyValues.equals(that.keyValues);
    }

    @Override
    public int hashCode() {
        return keyValues.hashCode();
    }

    public void forEach(final BiConsumer<? super KeyValueId, ? super KeyValue> action) {
        keyValues.forEach((str, keyValue)  -> {
            action.accept(new KeyValueId(UUID.fromString(str)), keyValue);
        });
    }
}
