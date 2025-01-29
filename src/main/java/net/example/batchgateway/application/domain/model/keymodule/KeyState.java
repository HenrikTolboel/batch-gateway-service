package net.example.batchgateway.application.domain.model.keymodule;

public record KeyState(KeyStateEnum value) {
    public static final KeyState active = from(KeyStateEnum.ACTIVE);
    public static final KeyState pre_active = from(KeyStateEnum.PREACTIVE);
    public static final KeyState deactivated = from(KeyStateEnum.DEACTIVATED);
    public static final KeyState destroyed = from(KeyStateEnum.DESTROYED);

    private static KeyState from(final KeyStateEnum keyStateEnum) {
        return new KeyState(keyStateEnum);
    }
}
