package net.example.batchgateway.application.domain.model.keymodule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class KeyValueTest {


    @Test
    void initExistingThrowsWhenNullParameter() {
        assertThrows(NullPointerException.class,
                () -> KeyValue.initExisting(null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null));
    }

    @Test
    void create() {
    }

    @Test
    void testCreate() {
    }

    private static Stream<Arguments> activateRules() {
        return Stream.of(
                arguments(KeyState.pre_active, true, KeyState.active),
                arguments(KeyState.active, false, KeyState.active),
                arguments(KeyState.deactivated, true, KeyState.active),
                arguments(KeyState.destroyed, false, KeyState.destroyed)
        );
    }

    @ParameterizedTest
    @MethodSource("activateRules")
    void activate(final KeyState existing, final boolean expectedResult, final KeyState expected) {
        final KeyValueDetails keyValueDetails = Mockito.mock(KeyValueAES.class);

        final KeyValue keyValue = KeyValue.initExisting(
                KeyValueId.generate(), existing, Instant.now(), Instant.now(),
                new KeyRestrictions(123, true),
                new KeyOrigin("origin"),
                keyValueDetails);

        assertNotNull(keyValue);
        assertEquals(existing, keyValue.getKeyState());

        final boolean result = keyValue.activate();

        assertEquals(expectedResult, result);

        assertEquals(expected, keyValue.getKeyState());

        verify(keyValueDetails, times(0)).destroyValue();
    }

    private static Stream<Arguments> deactivateRules() {
        return Stream.of(
                arguments(KeyState.pre_active, false, KeyState.pre_active),
                arguments(KeyState.active, true, KeyState.deactivated),
                arguments(KeyState.deactivated, false, KeyState.deactivated),
                arguments(KeyState.destroyed, false, KeyState.destroyed)
        );
    }

    @ParameterizedTest
    @MethodSource("deactivateRules")
    void deactivate(final KeyState existing, final boolean expectedResult, final KeyState expected) {
        final KeyValueDetails keyValueDetails = Mockito.mock(KeyValueAES.class);

        final KeyValue keyValue = KeyValue.initExisting(
                KeyValueId.generate(), existing, Instant.now(), Instant.now(),
                new KeyRestrictions(123, true),
                new KeyOrigin("origin"),
                keyValueDetails);

        assertNotNull(keyValue);
        assertEquals(existing, keyValue.getKeyState());

        final boolean result = keyValue.deactivate();

        assertEquals(expectedResult, result);

        assertEquals(expected, keyValue.getKeyState());

        verify(keyValueDetails, times(0)).destroyValue();
    }

    private static Stream<Arguments> destroyRules() {
        return Stream.of(
                arguments(KeyState.pre_active, true, KeyState.destroyed, 1),
                arguments(KeyState.active, true, KeyState.destroyed, 1),
                arguments(KeyState.deactivated, true, KeyState.destroyed, 1),
                arguments(KeyState.destroyed, false, KeyState.destroyed, 0)
        );
    }

    @ParameterizedTest
    @MethodSource("destroyRules")
    void destroy(final KeyState existing, final boolean expectedResult, final KeyState expected, final int destroyValueCalledTimes) {
        final KeyValueDetails keyValueDetails = Mockito.mock(KeyValueAES.class);

        final KeyValue keyValue = KeyValue.initExisting(
                KeyValueId.generate(), existing, Instant.now(), Instant.now(),
                new KeyRestrictions(123, true),
                new KeyOrigin("origin"),
                keyValueDetails);

        assertNotNull(keyValue);
        assertEquals(existing, keyValue.getKeyState());

        final boolean result = keyValue.destroy();

        assertEquals(expectedResult, result);

        assertEquals(expected, keyValue.getKeyState());

        verify(keyValueDetails, times(destroyValueCalledTimes)).destroyValue();
    }
}
