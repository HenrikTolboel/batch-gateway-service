package net.example.batchgateway.application.domain.model;

import net.example.batchgateway.application.domain.model.keymodule.KeyRevisionId;

public record EncryptedData(String value, KeyRevisionId revisionId) {
}
