package scenes.gamescene.systems;

import core.EngineContext;
import core.ecs.Ecs;
import core.ecs.RunnableSystem;
import core.ecs.components.Position;
import core.util.InterpolateHelper;
import core.util.Vector2f;
import scenes.lib.AnimationConstants;
import scenes.lib.components.PathFindingTarget;
import scenes.lib.components.SpriteAnimation;
import scenes.lib.components.UnitInfo;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MovementSystem implements RunnableSystem {

    private final Ecs ecs;

    public MovementSystem(EngineContext context) {
        ecs = context.getService(Ecs.class);
    }

    public void update(double delta) {
        var units = ecs.view(Position.class, UnitInfo.class, PathFindingTarget.class);

        for(var unit : units) {

            var currentPosition = unit.component1().position();
            var newPos = InterpolateHelper.interpolateLinear(
                    currentPosition.toVector2fxz(),
                    unit.component3().target(),
                    unit.component2().movementSpeed() * 0.001f);
            var movement = currentPosition.toVector2fxz().sub(newPos);
            if(Math.abs(movement.x()) <= 0.001 && Math.abs(movement.y()) <= 0.001) {
                ecs.removeComponent(unit.entityId(), PathFindingTarget.class);
            }

            animateUnit(unit.entityId(), movement);
            ecs.setComponent(unit.entityId(), new Position(newPos.toVector3fy(currentPosition.y())));

        }

    }

    private void animateUnit(int entityId, Vector2f movement) {
        var spriteAnimation = ecs.<SpriteAnimation>getComponent(entityId, SpriteAnimation.class);

        var directions = List.of(
                Map.entry(movement.x()*-1, AnimationConstants.RIGHT),
                Map.entry(movement.x(), AnimationConstants.LEFT),
                Map.entry(movement.y()*-1, AnimationConstants.DOWN),
                Map.entry(movement.y(), AnimationConstants.UP)
        );

        var generalDirection = directions.stream().max(Map.Entry.comparingByKey());

        if(generalDirection.isPresent()) {
            ecs.setComponent(entityId, spriteAnimation.play(generalDirection.get().getValue()));
        }
    }

}
