package net.example.batchgateway.application.domain.model;

public record BatchName(String value) {

    public BatchName {
        value = NameUtils.validName(value);
    }

    public static BatchName create(final String value) {
        return new BatchName(value);
    }

}
