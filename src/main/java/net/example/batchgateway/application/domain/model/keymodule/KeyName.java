package net.example.batchgateway.application.domain.model.keymodule;

import net.example.batchgateway.application.domain.model.NameUtils;

public record KeyName(String value) {

    public KeyName {
        value = NameUtils.validName(value);
    }

    public static KeyName create(final String value) {
        return new KeyName(value);
    }
}
