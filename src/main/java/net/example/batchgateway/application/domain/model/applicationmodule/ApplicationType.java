package net.example.batchgateway.application.domain.model.applicationmodule;

public record ApplicationType(ApplicationTypeEnum value) {

    public static ApplicationType create(final ApplicationTypeEnum value) {
        return new ApplicationType(value);
    }

    public static ApplicationType create(final String value) {
        return new ApplicationType(ApplicationTypeEnum.valueOf(value));
    }

}
