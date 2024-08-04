package scenes.gamescene.systems;

import core.EngineContext;
import core.ecs.Ecs;
import core.ecs.EcsView2;
import core.ecs.RunnableSystem;
import scenes.gamescene.ClientHandler;
import scenes.lib.components.Command;
import scenes.lib.components.NetSynch;

import java.util.ArrayList;
import java.util.List;

public class ClientSystem implements RunnableSystem {

    private final List<EcsView2<Command, NetSynch>> receivedCommands = new ArrayList<>();

    private final ClientHandler clientHandler;
    private final Ecs ecs;

    public ClientSystem(EngineContext context, ClientHandler clientHandler) {
        ecs = context.getService(Ecs.class);
        this.clientHandler = clientHandler;

        this.clientHandler.bindReceivedCommands(msg -> {
           receivedCommands.addAll(msg.commands());
           System.out.println("Received commands: " + msg.commands().size());
        });

    }

    public void update() {
        var commands = ecs.view(Command.class, NetSynch.class)
                .stream()
                .filter(entry -> !entry.component1().sent())
                .toList();

        if(commands.isEmpty()) {
            return;
        }

        clientHandler.sendCommandList(commands);

        for(var command : commands) {
            ecs.setComponent(command.entityId(), command.component1().setSent());
        }

    }

}
