package net.example.batchgateway.adapter.output.generatekeymaterial;

import io.micrometer.observation.ObservationRegistry;
import net.example.batchgateway.application.domain.model.keymodule.KeyMaterial3DES;
import net.example.batchgateway.application.domain.model.keymodule.KeyMaterialAES;
import net.example.batchgateway.application.domain.model.keymodule.KeyMaterialLMS;
import net.example.batchgateway.application.domain.model.keymodule.KeyMaterialML_DSA;
import net.example.batchgateway.application.domain.model.keymodule.KeyMaterialXMSS;
import net.example.batchgateway.application.domain.model.keymodule.KeyValue3DES;
import net.example.batchgateway.application.domain.model.keymodule.KeyValueAES;
import net.example.batchgateway.application.domain.model.keymodule.KeyValueDetails;
import net.example.batchgateway.application.domain.model.keymodule.KeyValueLMS;
import net.example.batchgateway.application.domain.model.keymodule.KeyValueML_DSA;
import net.example.batchgateway.application.domain.model.keymodule.KeyValueXMSS;
import net.example.batchgateway.application.port.GeneralError;
import net.example.batchgateway.application.port.ServiceError;
import net.example.batchgateway.application.port.output.GenerateKeyMaterialCommand;
import net.example.batchgateway.application.port.output.GenerateKeyMaterialPort;
import net.example.utils.dichotomy.Result;
import org.springframework.stereotype.Service;

@Service
class GenerateKeyMaterial implements GenerateKeyMaterialPort {

    private final ObservationRegistry observationRegistry;

    public GenerateKeyMaterial(final ObservationRegistry observationRegistry) {
        this.observationRegistry = observationRegistry;
    }

    @Override
    public Result<KeyValueDetails, GeneralError> generateKeyMaterial(final GenerateKeyMaterialCommand command) {

        switch (command.keyMaterialDetails()) {
            case KeyMaterialAES material -> {
                return Result.ofOK(doGenerateKeyMaterial(material));
            }
            case KeyMaterial3DES material -> {
                return Result.ofOK(doGenerateKeyMaterial(material));
            }
            case KeyMaterialLMS material -> {
                return Result.ofOK(doGenerateKeyMaterial(material));
            }
            case KeyMaterialXMSS material -> {
                return Result.ofOK(doGenerateKeyMaterial(material));
            }
            case KeyMaterialML_DSA material -> {
                return Result.ofOK(doGenerateKeyMaterial(material));
            }
            default -> {
                return Result.ofErr(new ServiceError.ReportApiError(new RuntimeException("alkssaklsakl")));
            }
        }
    }

    private KeyValueAES doGenerateKeyMaterial(final KeyMaterialAES keyMaterialAES) {
        return new KeyValueAES(keyMaterialAES.size(),
                keyMaterialAES.kcvType(),
                keyMaterialAES.kcvLength(),
                "jdskldjghagsjhaklsdaklkl-KeyValueAES".getBytes());


    }

    private KeyValue3DES doGenerateKeyMaterial(final KeyMaterial3DES keyMaterial3DES) {
        return new KeyValue3DES(keyMaterial3DES.size(),
                keyMaterial3DES.kcvType(),
                keyMaterial3DES.kcvLength(),
                "jdskldjklsdaklshasgjkl-KeyValue3DES".getBytes());


    }

    private KeyValueLMS doGenerateKeyMaterial(final KeyMaterialLMS keyMaterialLMS) {
        return new KeyValueLMS(keyMaterialLMS.size(),
                keyMaterialLMS.kcvType(),
                keyMaterialLMS.kcvLength(),
                "jdskldjghasadasgsjhaklsdaklkl-KeyValueLMS".getBytes());


    }

    private KeyValueXMSS doGenerateKeyMaterial(final KeyMaterialXMSS keyMaterialXMSS) {
        return new KeyValueXMSS(keyMaterialXMSS.size(),
                keyMaterialXMSS.kcvType(),
                keyMaterialXMSS.kcvLength(),
                "jdskldjghasadasgsjhakhjhlsdaklkl-KeyValueXMSS".getBytes());


    }

    private KeyValueML_DSA doGenerateKeyMaterial(final KeyMaterialML_DSA keyMaterialML_DSA) {
        return new KeyValueML_DSA(keyMaterialML_DSA.size(),
                keyMaterialML_DSA.kcvType(),
                keyMaterialML_DSA.kcvLength(),
                "jdsklllæklældjghasadasgsjhaklsdaklkl-KeyValueML_DSA".getBytes());


    }

}
