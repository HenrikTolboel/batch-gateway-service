package net.example.batchgateway.adapter.input.web.dto;

import net.example.batchgateway.application.domain.model.keymodule.KcvType;
import net.example.batchgateway.application.domain.model.keymodule.KcvTypeEnum;

import java.util.Objects;

public record KcvTypeDTO(String value) {

    public KcvTypeDTO {
        Objects.requireNonNull(value);
        KcvTypeEnum kt = KcvTypeEnum.valueOf(value);
    }

    public static KcvTypeDTO create(final String keyType) {
        return new KcvTypeDTO(keyType);
    }

    public static KcvTypeDTO create(final KcvType kcvType) {
        return new KcvTypeDTO(kcvType.value().name());
    }
}
