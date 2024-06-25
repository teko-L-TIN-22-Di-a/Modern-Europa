package scenes.lib.networking;

public record RegisterMessage(String username) {
    public static final String TYPE = RegisterMessage.class.getSimpleName();
}
