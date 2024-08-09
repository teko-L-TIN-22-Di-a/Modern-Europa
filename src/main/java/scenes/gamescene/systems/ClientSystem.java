package scenes.gamescene.systems;

import core.EngineContext;
import core.ecs.*;
import scenes.gamescene.ClientHandler;
import scenes.lib.components.Command;
import java.util.ArrayList;
import java.util.List;

public class ClientSystem implements RunnableSystem {

    private final List<EcsView<Command>> receivedCommands = new ArrayList<>();

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

    public void update(double delta) {

        synchroniseCommands();

        var commands = ecs.view(Command.class)
                .stream()
                .filter(entry -> !entry.component().sent())
                .toList();

        if(commands.isEmpty()) {
            return;
        }

        clientHandler.sendCommandList(commands);

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
            entity.setComponent(command.component().setSent());
        }

    }

}
