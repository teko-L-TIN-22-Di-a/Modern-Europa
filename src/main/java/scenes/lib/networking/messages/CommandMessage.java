package scenes.lib.networking.messages;

import core.ecs.EcsView;
import scenes.lib.components.Command;

import java.util.List;

public record CommandMessage(List<EcsView<Command>> commands) {
    public static final String TYPE = CommandMessage.class.getSimpleName();
}
