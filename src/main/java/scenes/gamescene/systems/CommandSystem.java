package scenes.gamescene.systems;

import core.EngineContext;
import core.ecs.*;
import core.ecs.components.Position;
import core.util.Vector2f;
import core.util.Vector3f;
import scenes.gamescene.commands.CommandConstants;
import scenes.lib.AnimationConstants;
import scenes.lib.components.*;
import scenes.lib.helper.EntityHelper;

import java.util.List;

public class CommandSystem implements RunnableSystem {

    private final Ecs ecs;

    public CommandSystem(EngineContext context) {
        ecs = context.getService(Ecs.class);
    }

    public void update(double delta) {
        var commands = ecs.view(Command.class);

        var units = ecs.view(Position.class, UnitInfo.class);

        for(var command : commands) {

            if(command.component().processed()) {

                if(command.component().isAlive()) {
                    ecs.delete(command.entityId());
                }

                ecs.setComponent(command.entityId(), command.component().tick());
                continue;
            }

            switch (command.component().commandType()) {
                case CommandConstants.MOVEMENT_TARGET:
                    resolveMovementCommand(command.component(), units);
                    break;
                case CommandConstants.BUILDING_CREATION:
                    resolveBuildingCreation(command.component());
                    break;
                case CommandConstants.AUTO_ATTACK:
                    resolveAutoAttack(command.component(), units);
                    break;
                case CommandConstants.MINING:
                    resolveMiningCommand(command.component());
                    break;
            }

            ecs.setComponent(command.entityId(), command.component().setProcessed());
        }

    }

    private void resolveMiningCommand(Command command) {
        var parameters = command.parameters();
        var amount = parameters.getDouble(CommandConstants.MINING_AMOUNT);
        var playerId = parameters.getInt(CommandConstants.MINING_PLAYER_ID);

        var currentInfo = ecs.view(PlayerResources.class)
                .stream().filter(info -> info.component().playerId() == playerId)
                .findFirst();

        if(currentInfo.isEmpty()) {
            return;
        }

        ecs.setComponent(currentInfo.get().entityId(), currentInfo.get().component().addResources((int)amount));
    }

    private void resolveMovementCommand(Command command, List<EcsView2<Position, UnitInfo>> units) {
        var parameters = command.parameters();
        var targetPos = parameters.<Vector2f>get(CommandConstants.MOVEMENT_TARGET_POSITION);
        var unitId = parameters.getString(CommandConstants.MOVEMENT_TARGET_UNIT);

        var unit = units.stream().filter(x -> x.component2().uuid().equals(unitId)).findFirst();

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

        entity = EntityHelper.createConstructionSite(ecs, playerId, buildingType, unitId);
        entity.setComponent(new Position(targetPos.toVector3fy(0)));
    }

    private void resolveAutoAttack(Command command, List<EcsView2<Position, UnitInfo>> units) {
        var parameters = command.parameters();
        var origin = parameters.<Vector3f>get(CommandConstants.AUTO_ATTACK_ORIGIN);
        var damage = parameters.getDouble(CommandConstants.AUTO_ATTACK_TARGET_DAMAGE);
        var targetUnitId = parameters.<String>get(CommandConstants.AUTO_ATTACK_TARGET_ENTITY_ID);

        var targetUnit = units.stream().filter(x -> x.component2().uuid().equals(targetUnitId)).findFirst();

        if(!targetUnit.isPresent()) {
            return;
        }

        ecs.setComponent(targetUnit.get().entityId(), targetUnit.get().component2().TakeDamage(damage));

        var exploder = ecs.newEntity();
        exploder.setComponent(new Position(origin.add(0, 0.2f, 0)));
        exploder.setComponent(new Sprite(null, Vector2f.of(2, 2), true));
        exploder.setComponent(SpriteAnimation.of(AnimationConstants.ATTACK_ANIMATIONS));
        exploder.setComponent(new AttackParticle(targetUnit.get().component1().position()));
    }

}
