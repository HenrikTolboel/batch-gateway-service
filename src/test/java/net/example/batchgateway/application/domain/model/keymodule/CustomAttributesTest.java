package net.example.batchgateway.application.domain.model.keymodule;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CustomAttributesTest {

    @Test
    void emptyCustomAttributesIsEmpty() {
        final CustomAttributes customAttributes = CustomAttributes.empty();
        final Map<String, Object> customMap = customAttributes.getMap();
        assertTrue(customMap.isEmpty());
    }

    @Test
    void getMapReturnsImmutableMap() {
        final CustomAttributes customAttributes = CustomAttributes.empty();
        final Map<String, Object> customMap = customAttributes.getMap();
        assertThrows(UnsupportedOperationException.class, () -> customMap.put("foo", "bar"));
    }

    @Test
    void constructorGivesImmutableMapWithGetMap() {
        final Map<String, Object> testMap = new HashMap<>();
        testMap.put("foo", "bar");
        final CustomAttributes customAttributes = new CustomAttributes(testMap);
        final Map<String, Object> customMap = customAttributes.getMap();
        assertFalse(customMap.isEmpty());
        assertEquals("bar", customMap.get("foo"));
        assertThrows(UnsupportedOperationException.class, () -> customMap.put("foo1", "bar1"));
    }

    @Test
    void factoryGivesImmutableMapWithGetMap() {
        final Map<String, Object> testMap = new HashMap<>();
        testMap.put("foo", "bar");
        final CustomAttributes customAttributes = CustomAttributes.fromMap(testMap);
        final Map<String, Object> customMap = customAttributes.getMap();
        assertFalse(customMap.isEmpty());
        assertEquals("bar", customMap.get("foo"));
        assertThrows(UnsupportedOperationException.class, () -> customMap.put("foo1", "bar1"));
    }

}
