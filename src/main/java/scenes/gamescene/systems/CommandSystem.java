package scenes.gamescene.systems;

import core.EngineContext;
import core.ecs.Ecs;
import core.ecs.RunnableSystem;
import core.ecs.components.Position;
import core.util.InterpolateHelper;
import core.util.Vector2f;
import scenes.gamescene.commands.CommandConstants;
import scenes.lib.components.Command;
import scenes.lib.components.PathFindingTarget;
import scenes.lib.components.UnitInfo;

public class CommandSystem implements RunnableSystem {

    private final Ecs ecs;

    public CommandSystem(EngineContext context) {
        ecs = context.getService(Ecs.class);
    }

    public void update() {
        var commands = ecs
                .view(Command.class)
                .stream()
                .filter(command -> !command.component().processed())
                .toList();

        var units = ecs.view(UnitInfo.class);

        for(var command : commands) {
            switch (command.component().commandType()) {
                case CommandConstants.MOVEMENT_TARGET:

                    var parameters = command.component().parameters();
                    var targetPos = parameters.<Vector2f>get(CommandConstants.MOVEMENT_TARGET_POSITION);
                    var unitId = parameters.getString(CommandConstants.MOVEMENT_TARGET_UNIT);

                    var unit = units.stream().filter(x -> x.component().uuid().equals(unitId)).findFirst();

                    if(unit.isPresent()) {
                        ecs.setComponent(unit.get().entityId(), new PathFindingTarget(targetPos));
                    }

                    break;
            }

            ecs.setComponent(command.entityId(), command.component().setProcessed());
        }

    }

}