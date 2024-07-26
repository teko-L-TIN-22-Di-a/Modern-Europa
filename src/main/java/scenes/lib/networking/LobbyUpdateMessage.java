package scenes.lib.networking;

import scenes.lib.PlayerInfo;

import java.util.List;

public record LobbyUpdateMessage(List<PlayerInfo> players) {
    public static final String TYPE = LobbyUpdateMessage.class.getSimpleName();
}
