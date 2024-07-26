package scenes.lib.networking;

import core.ecs.EcsSnapshot;
import scenes.lib.PlayerInfo;

import java.util.List;

public record InitGameMessage(EcsSnapshot ecsSnapshot) {
    public static final String TYPE = InitGameMessage.class.getSimpleName();
}
