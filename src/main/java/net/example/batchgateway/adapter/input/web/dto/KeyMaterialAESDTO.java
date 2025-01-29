package net.example.batchgateway.adapter.input.web.dto;

import net.example.batchgateway.application.domain.model.keymodule.KcvType;
import net.example.batchgateway.application.domain.model.keymodule.KeyMaterialAES;
import net.example.batchgateway.application.domain.model.keymodule.KeyMaterialDetails;

public record KeyMaterialAESDTO(
        int size,
        KcvTypeDTO kcvType,
        int kcvLength
) implements KeyMaterialDetailsDTO {
    public KeyMaterialAESDTO {
        if (size != 128 && size != 192 && size != 256) {
            throw new IllegalArgumentException("size must be 128 or 192 or 256");
        }
        if (kcvLength <= 0) {
            throw new IllegalArgumentException("kcvLength must be greater than 0");
        }

    }

    @Override
    public KeyMaterialDetails toKeyMaterialDetails() {
        return new KeyMaterialAES(size(), KcvType.create(kcvType().value()), kcvLength());
    }
}
