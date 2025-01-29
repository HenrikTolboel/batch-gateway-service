package net.example.batchgateway.adapter.output.generatekeymaterial;

import io.micrometer.observation.ObservationRegistry;
import net.example.batchgateway.application.domain.model.keymodule.KcvType;
import net.example.batchgateway.application.domain.model.keymodule.KcvTypeEnum;
import net.example.batchgateway.application.domain.model.keymodule.KeyMaterial3DES;
import net.example.batchgateway.application.domain.model.keymodule.KeyMaterialAES;
import net.example.batchgateway.application.domain.model.keymodule.KeyValue3DES;
import net.example.batchgateway.application.domain.model.keymodule.KeyValueAES;
import net.example.batchgateway.application.domain.model.keymodule.KeyValueDetails;
import net.example.batchgateway.application.domain.model.tenantmodule.TenantId;
import net.example.batchgateway.application.port.output.GenerateKeyMaterialCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class GenerateKeyMaterialTest {

    private final ObservationRegistry observationRegistry = mock(ObservationRegistry.class);

    private final GenerateKeyMaterial generateKeyMaterial =
            new GenerateKeyMaterial(observationRegistry);

    @Test
    void generateKeyMaterialAES() {

        // GIVEN
        final KeyMaterialAES materialDetails = new KeyMaterialAES(192,
                KcvType.create(KcvTypeEnum.CMAC),
                1234);
        final GenerateKeyMaterialCommand generateKeyMaterialCommand = new GenerateKeyMaterialCommand(TenantId.generate(),
                materialDetails);

        // WHEN
        final var result = generateKeyMaterial.generateKeyMaterial(generateKeyMaterialCommand);

        // THEN

        assertTrue(result.isOK());
        assertTrue(result.ok().isPresent());
        final KeyValueDetails keyValueDetails = result.ok().get();

        assertInstanceOf(KeyValueAES.class, keyValueDetails);

        assertEquals(materialDetails.size(), ((KeyValueAES) keyValueDetails).getSize());
        assertEquals(materialDetails.kcvType(), ((KeyValueAES) keyValueDetails).getKcvType());
        assertEquals(materialDetails.kcvLength(), ((KeyValueAES) keyValueDetails).getKcvLength());
    }

    @Test
    void generateKeyMaterial3DES() {

        // GIVEN
        final KeyMaterial3DES materialDetails = new KeyMaterial3DES(192,
                KcvType.create(KcvTypeEnum.CMAC),
                1234);
        final GenerateKeyMaterialCommand generateKeyMaterialCommand = new GenerateKeyMaterialCommand(TenantId.generate(),
                materialDetails);

        // WHEN
        final var result = generateKeyMaterial.generateKeyMaterial(generateKeyMaterialCommand);

        // THEN

        assertTrue(result.isOK());
        assertTrue(result.ok().isPresent());
        final KeyValueDetails keyValueDetails = result.ok().get();

        assertInstanceOf(KeyValue3DES.class, keyValueDetails);

        assertEquals(materialDetails.size(), ((KeyValue3DES) keyValueDetails).getSize());
        assertEquals(materialDetails.kcvType(), ((KeyValue3DES) keyValueDetails).getKcvType());
        assertEquals(materialDetails.kcvLength(), ((KeyValue3DES) keyValueDetails).getKcvLength());
    }
}
