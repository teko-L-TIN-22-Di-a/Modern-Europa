package scenes.lib.components;

public record Powered(boolean powered) {
    public static Powered on() {
        return new Powered(true);
    }

    public static Powered off() {
        return new Powered(false);
    }
}
