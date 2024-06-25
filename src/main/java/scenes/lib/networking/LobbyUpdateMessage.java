package scenes.lib.networking;

import java.util.ArrayList;

public record LobbyUpdateMessage(ArrayList<String> users) {
    public static final String TYPE = LobbyUpdateMessage.class.getSimpleName();
}
