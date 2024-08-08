package scenes.lib.networking.messages;

import core.ecs.EcsView;
import core.ecs.EcsView2;
import scenes.lib.PlayerInfo;
import scenes.lib.components.Command;
import scenes.lib.components.NetSynch;

import java.util.List;

public record CommandMessage(List<EcsView<Command>> commands) {
    public static final String TYPE = CommandMessage.class.getSimpleName();
}
