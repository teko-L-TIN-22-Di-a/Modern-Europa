package scenes.lib.networking.messages;

public record RegisterMessage(String username) {
    public static final String TYPE = RegisterMessage.class.getSimpleName();
}
