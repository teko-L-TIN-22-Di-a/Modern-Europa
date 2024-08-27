package scenes.gamescene.systems;

import core.EngineContext;
import core.ecs.Ecs;
import core.ecs.RunnableSystem;
import scenes.lib.components.Generator;
import scenes.lib.components.Powered;
import scenes.lib.components.UnitInfo;

public class PowerSystem implements RunnableSystem {

    private final Ecs ecs;
    private final int playerId;

    public PowerSystem(EngineContext context, int playerId) {
        ecs = context.getService(Ecs.class);
        this.playerId = playerId;
    }

    @Override
    public void update(double delta) {

        var generators = ecs
                .view(Generator.class, UnitInfo.class)
                .stream().filter(entry -> entry.component2().playerId() == playerId)
                .toList();

        var powerEntities = ecs
                .view(Powered.class, UnitInfo.class)
                .stream().filter(entry -> entry.component2().playerId() == playerId)
                .toList();

        var availablePower = 0f;
        for (var generator : generators) {
            availablePower += generator.component1().energy();
        }

        for (var entity : powerEntities) {
            if(availablePower < 1) {
                ecs.setComponent(entity.entityId(), Powered.off());
                continue;
            }

            ecs.setComponent(entity.entityId(), Powered.on());
            availablePower--;
        }

    }

}
