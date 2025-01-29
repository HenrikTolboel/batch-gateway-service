package net.example.batchgateway.application.domain.model.keymodule;


import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "keyMaterialDetailsType")
public sealed interface KeyMaterialDetails permits KeyMaterial3DES, KeyMaterialAES/*, KeyMaterialEC, KeyMaterialHMAC, KeyMaterialRSA*/, KeyMaterialLMS, KeyMaterialXMSS, KeyMaterialML_DSA {
}

