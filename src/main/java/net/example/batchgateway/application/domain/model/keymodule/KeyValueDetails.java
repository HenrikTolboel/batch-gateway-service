package net.example.batchgateway.application.domain.model.keymodule;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "keyValueDetailsType")
public sealed interface KeyValueDetails permits KeyValue3DES, KeyValueAES, KeyValueEC, KeyValueHMAC, KeyValueRSA,
        KeyValueLMS, KeyValueXMSS, KeyValueML_DSA {
    Cipher getCipher();
    void destroyValue();
    KeyValueDetails clone();
}

