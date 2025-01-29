package net.example.batchgateway.application.domain.model.keymodule;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Map;
import java.util.Objects;

public final class CustomAttributes {

    private final Map<String, Object> customAttributes;

    @JsonCreator
    public CustomAttributes(final Map<String, Object> customAttributes) {
        Objects.requireNonNull(customAttributes);
        this.customAttributes = Map.copyOf(customAttributes);
    }

    public static CustomAttributes fromMap(final Map<String, Object> attributes) {
        return new CustomAttributes(attributes);
    }

    public static CustomAttributes empty() {
        return new CustomAttributes(Map.of());
    }

    public Map<String, Object> getMap() {
        return customAttributes;
    }

    protected CustomAttributes clone() {
        return new CustomAttributes(Map.copyOf(customAttributes));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CustomAttributes that = (CustomAttributes) o;
        return Objects.equals(customAttributes, that.customAttributes);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(customAttributes);
    }

}
