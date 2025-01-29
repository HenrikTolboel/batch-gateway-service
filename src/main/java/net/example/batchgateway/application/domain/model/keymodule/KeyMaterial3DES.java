package net.example.batchgateway.application.domain.model.keymodule;

public record KeyMaterial3DES(
        int size,
        KcvType kcvType,
        int kcvLength
) implements KeyMaterialDetails {
    public KeyMaterial3DES {
        if (size != 128 && size != 192 && size != 256) {
            throw new IllegalArgumentException("size must be 128 or 192 or 256");
        }
        if (kcvLength <= 0) {
            throw new IllegalArgumentException("kcvLength must be greater than 0");
        }

    }

}
