package net.example.batchgateway.adapter.input.web.dto;

import net.example.batchgateway.application.domain.model.Batch;

public record ViewBatchDTO(String batchId, String batchName
) {

    public static ViewBatchDTO create(final Batch batch) {
        return new ViewBatchDTO(batch.getId().value().toString(), batch.getBatchName().value());
    }

}
