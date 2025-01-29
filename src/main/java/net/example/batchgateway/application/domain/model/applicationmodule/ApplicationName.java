package net.example.batchgateway.application.domain.model.applicationmodule;

import net.example.batchgateway.application.domain.model.NameUtils;

public record ApplicationName(String value) {

    public ApplicationName {
        value = NameUtils.validName(value);
    }

    public static ApplicationName create(final String value) {
        return new ApplicationName(value);
    }

}
