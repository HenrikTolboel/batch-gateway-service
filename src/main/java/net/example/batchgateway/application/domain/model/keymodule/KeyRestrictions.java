package net.example.batchgateway.application.domain.model.keymodule;

public record KeyRestrictions(
        int PCI,
        boolean allowExportOfKey

        ) {
}
