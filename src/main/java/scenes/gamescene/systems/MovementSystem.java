package scenes.gamescene.systems;

import core.EngineContext;
import core.ecs.Ecs;
import core.ecs.RunnableSystem;
import core.ecs.components.Position;
import core.util.InterpolateHelper;
import scenes.lib.components.PathFindingTarget;
import scenes.lib.components.UnitInfo;

public class MovementSystem implements RunnableSystem {

    private final Ecs ecs;

    public MovementSystem(EngineContext context) {
        ecs = context.getService(Ecs.class);
    }

    public void update() {
        var units = ecs.view(Position.class, UnitInfo.class, PathFindingTarget.class);

        for(var unit : units) {

            var currentPosition = unit.component1().position();
            var newPos = InterpolateHelper.interpolateLinear(currentPosition.toVector2fxz(), unit.component3().target(), 0.1f);
            var movement = currentPosition.toVector2fxz().sub(newPos);
            if(Math.abs(movement.x()) <= 0.001 && Math.abs(movement.y()) <= 0.001) {
                ecs.removeComponent(unit.entityId(), PathFindingTarget.class);
            }

            ecs.setComponent(unit.entityId(), new Position(newPos.toVector3fy(currentPosition.y())));

        }

    }

}
