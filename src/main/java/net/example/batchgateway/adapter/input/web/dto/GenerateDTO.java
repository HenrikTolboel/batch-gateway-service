package net.example.batchgateway.adapter.input.web.dto;

import java.time.Instant;
import java.util.Map;

public record GenerateDTO(String keyName,
                          KeyTypeDTO keyType, // TODO: what is basicKeyType compared to this?
                          KeyMaterialDetailsDTO keyMaterialDetails,
                          Instant expire,
                          boolean autoActivate,
                          boolean autoRotate,
                          int keyLifeCycleDays,
                          Map<String, Object> customAttributes
) {
}
