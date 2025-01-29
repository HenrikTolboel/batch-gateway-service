package net.example.batchgateway.adapter.input.web.dto;

import net.example.batchgateway.application.domain.model.keymodule.KcvType;
import net.example.batchgateway.application.domain.model.keymodule.KeyMaterial3DES;

public record KeyMaterial3DESDTO(
        int size,
        KcvTypeDTO kcvType,
        int kcvLength
) implements KeyMaterialDetailsDTO {
    public KeyMaterial3DESDTO {
        if (size != 128 && size != 192 && size != 256) {
            throw new IllegalArgumentException("size must be 128 or 192 or 256");
        }
        if (kcvLength <= 0) {
            throw new IllegalArgumentException("kcvLength must be greater than 0");
        }

    }

    @Override
    public KeyMaterial3DES toKeyMaterialDetails() {
        return new KeyMaterial3DES(size(), KcvType.create(kcvType().value()), kcvLength());
    }

}
