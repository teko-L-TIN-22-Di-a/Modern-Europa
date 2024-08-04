package scenes.gamescene.systems;

import core.EngineContext;
import core.ecs.Ecs;
import core.ecs.EcsView2;
import core.ecs.RunnableSystem;
import scenes.gamescene.ServerHandler;
import scenes.lib.components.Command;
import scenes.lib.components.NetSynch;

import java.util.ArrayList;
import java.util.List;

public class ServerSystem implements RunnableSystem {

    private List<EcsView2<Command, NetSynch>> receivedCommands = new ArrayList<>();

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

        if(!receivedCommands.isEmpty()) {
            var existingEntries = ecs.view(NetSynch.class);

            for(var entry : receivedCommands) {
                var existingEntry = existingEntries.stream()
                        .filter(x -> x.Component().uuid().equals(entry.component2().uuid()))
                        .findFirst();

                if(!existingEntry.isPresent()) {
                    continue;
                }

                ecs.setComponent(existingEntry.get().entityId(), entry.component1().setSent());
            }

            receivedCommands.clear();
        }

        if(sleepTimeout > 0) {
            sleepTimeout--;
            return;
        }

        sleepTimeout = SLEEP_TIMEOUT;

        var commands = ecs.view(Command.class, NetSynch.class)
                .stream()
                .filter(entry -> !entry.component1().sent())
                .toList();

        if(commands.isEmpty()) {
            return;
        }

        serverHandler.sendCommandList(commands);
        System.out.println("Pushing server side update!");

        for(var command : commands) {
            ecs.setComponent(command.entityId(), command.component1().setSent());
        }

    }

}
