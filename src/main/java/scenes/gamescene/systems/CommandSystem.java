package scenes.gamescene.systems;

import core.EngineContext;
import core.ecs.Ecs;
import core.ecs.EcsView;
import core.ecs.Entity;
import core.ecs.RunnableSystem;
import core.ecs.components.Position;
import core.util.InterpolateHelper;
import core.util.Vector2f;
import scenes.gamescene.commands.CommandConstants;
import scenes.lib.components.Command;
import scenes.lib.components.PathFindingTarget;
import scenes.lib.components.UnitInfo;
import scenes.lib.entities.EntityHelper;

import java.util.List;

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
                    resolveMovementCommand(command.component(), units);
                    break;
                case CommandConstants.BUILDING_CREATION:
                    resolveBuildingCreation(command.component());
                    break;
            }

            ecs.setComponent(command.entityId(), command.component().setProcessed());
        }

    }

    private void resolveMovementCommand(Command command, List<EcsView<UnitInfo>> units) {
        var parameters = command.parameters();
        var targetPos = parameters.<Vector2f>get(CommandConstants.MOVEMENT_TARGET_POSITION);
        var unitId = parameters.getString(CommandConstants.MOVEMENT_TARGET_UNIT);

        var unit = units.stream().filter(x -> x.component().uuid().equals(unitId)).findFirst();

        if(unit.isPresent()) {
            ecs.setComponent(unit.get().entityId(), new PathFindingTarget(targetPos));
        }
    }

    private void resolveBuildingCreation(Command command) {
        var parameters = command.parameters();
        var buildingType = parameters.<String>get(CommandConstants.BUILDING_CREATION_TYPE);
        var playerId = parameters.getInt(CommandConstants.BUILDING_CREATION_PLAYER_ID);
        var targetPos = parameters.<Vector2f>get(CommandConstants.BUILDING_CREATION_POSITION);
        var unitId = parameters.<String>get(CommandConstants.BUILDING_CREATION_ID);

        Entity entity;

        switch(buildingType) {
            case UnitInfo.BASE:
                entity = EntityHelper.createMainBase(ecs, playerId, unitId);
                break;
            case UnitInfo.GENERATOR:
                entity = EntityHelper.createGenerator(ecs, playerId, unitId);
                break;
            case UnitInfo.Miner:
                entity = EntityHelper.createMiner(ecs, playerId, unitId);
                break;
            default:
                return;
        }

        entity.setComponent(new Position(targetPos.toVector3fy(0)));

    }

}
