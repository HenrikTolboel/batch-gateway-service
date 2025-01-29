package net.example.batchgateway.application.domain.model.keymodule;

public record KeyType(KeyTypeEnum value) {

    public static KeyType create(KeyTypeEnum value) {
        return new KeyType(value);
    }

    public static KeyType create(String value) {
        return new KeyType(KeyTypeEnum.valueOf(value));
    }
}
