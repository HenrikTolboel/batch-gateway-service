package net.example.batchgateway.adapter.input.web.dto;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import net.example.batchgateway.application.domain.model.keymodule.KeyMaterialDetails;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "keyMaterialDetailsDTOType")
public sealed interface KeyMaterialDetailsDTO permits KeyMaterial3DESDTO, KeyMaterialAESDTO /*, KeyMaterialAES, KeyMaterialEC, KeyMaterialHMAC, KeyMaterialRSA, KeyMaterialLMS, KeyMaterialXMSS, KeyMaterialML_DSA*/ {

    KeyMaterialDetails toKeyMaterialDetails();
}

