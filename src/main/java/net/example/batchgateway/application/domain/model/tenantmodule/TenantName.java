package net.example.batchgateway.application.domain.model.tenantmodule;

import net.example.batchgateway.application.domain.model.NameUtils;

public record TenantName(String value) {

    public TenantName {
        value = NameUtils.validName(value);
    }

    public static TenantName create(String value) {
        return new TenantName(value);
    }

}
