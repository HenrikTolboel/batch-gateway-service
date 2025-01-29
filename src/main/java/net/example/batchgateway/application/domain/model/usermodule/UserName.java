package net.example.batchgateway.application.domain.model.usermodule;

import net.example.batchgateway.application.domain.model.NameUtils;

public record UserName(String value) {

    public UserName {
        value = NameUtils.validName(value);
    }
}
