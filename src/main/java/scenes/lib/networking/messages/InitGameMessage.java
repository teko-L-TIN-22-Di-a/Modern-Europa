package scenes.lib.networking.messages;

import core.ecs.EcsSnapshot;

public record InitGameMessage(EcsSnapshot ecsSnapshot) {
    public static final String TYPE = InitGameMessage.class.getSimpleName();
}
