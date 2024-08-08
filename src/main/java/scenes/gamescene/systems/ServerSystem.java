package scenes.gamescene.systems;

import core.EngineContext;
import core.ecs.Ecs;
import core.ecs.EcsView;
import core.ecs.EcsView2;
import core.ecs.RunnableSystem;
import scenes.gamescene.ServerHandler;
import scenes.lib.components.Command;
import scenes.lib.components.NetSynch;

import java.util.ArrayList;
import java.util.List;

public class ServerSystem implements RunnableSystem {

    private List<EcsView<Command>> receivedCommands = new ArrayList<>();

    private static final int SLEEP_TIMEOUT = 8;
    private final ServerHandler serverHandler;
    private final Ecs ecs;

    private int sleepTimeout;

    public ServerSystem(EngineContext context, ServerHandler serverHandler) {
        ecs = context.getService(Ecs.class);
        this.serverHandler = serverHandler;

        this.serverHandler.bindReceivedCommands(msg -> {
            receivedCommands.addAll(msg.commands());
            System.out.println("Received commands: " + msg.commands().size());
        });
    }

    public void update() {

        synchroniseCommands();

        if(sleepTimeout > 0) {
            sleepTimeout--;
            return;
        }

        sleepTimeout = SLEEP_TIMEOUT;

        var commands = ecs.view(Command.class)
                .stream()
                .filter(entry -> !entry.component().sent())
                .toList();

        if(commands.isEmpty()) {
            return;
        }

        serverHandler.sendCommandList(commands.stream().map(command -> new EcsView<Command>(command.entityId(), command.component().setProcessed(false))).toList());

        for(var command : commands) {
            ecs.setComponent(command.entityId(), command.component().setSent());
        }

    }

    private void synchroniseCommands() {

        if(receivedCommands.isEmpty()) {
            return;
        }

        var commands = ecs.view(Command.class);

        var receivedCommands = List.copyOf(this.receivedCommands);
        this.receivedCommands.clear();

        for(var command : receivedCommands) {
            if(commands.stream().anyMatch(x -> x.component().commandId().equals(command.component().commandId()))) {
                continue;
            }

            var entity = ecs.newEntity();
            entity.setComponent(command.component());
        }

    }

}
