package net.example.batchgateway.adapter.input.web.dto;

import net.example.batchgateway.application.domain.model.keymodule.KeyType;
import net.example.batchgateway.application.domain.model.keymodule.KeyTypeEnum;

import java.util.Objects;

public record KeyTypeDTO(String keyType) {

    public KeyTypeDTO {
        Objects.requireNonNull(keyType);
        KeyTypeEnum kt = KeyTypeEnum.valueOf(keyType);

    }

    public static KeyTypeDTO create(final String keyType) {
        return new KeyTypeDTO(keyType);
    }

    public static KeyTypeDTO create(final KeyType keyType) {
        return new KeyTypeDTO(keyType.value().name());
    }
}
