package net.example.batchgateway.config;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CurrentRequestHeaders {

    private final ThreadLocal<Map<String, String>> requestHeaders = new ThreadLocal<>();



    public void set(final Map<String, String> headers) {
        requestHeaders.set(headers);
    }

    public Map<String, String> get() {
        return requestHeaders.get();
    }

    public void remove() {
        requestHeaders.remove();
    }
}

