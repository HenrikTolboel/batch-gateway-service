package net.example.batchgateway.application.domain.model.keymodule;

public record KcvType(KcvTypeEnum value) {

    public static KcvType create(final KcvTypeEnum value) {
        return new KcvType(value);
    }

    public static KcvType create(final String value) {
        return new KcvType(KcvTypeEnum.valueOf(value));
    }

}
