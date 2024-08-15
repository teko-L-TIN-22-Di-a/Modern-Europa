package scenes.gamescene.systems;

import core.EngineContext;
import core.ecs.Ecs;
import core.ecs.Entity;
import core.ecs.RunnableSystem;
import core.ecs.components.Position;
import scenes.lib.components.Construction;
import scenes.lib.components.UnitInfo;
import scenes.lib.helper.EntityHelper;

public class ConstructionSystem implements RunnableSystem {

    private final Ecs ecs;

    public ConstructionSystem(EngineContext context) {
        ecs = context.getService(Ecs.class);
    }

    public void update(double delta) {
        var constructionUnits = ecs
                .view(Construction.class, Position.class, UnitInfo.class);

        for(var unit : constructionUnits) {

           if(unit.component1().buildTime() > 0) {
               ecs.setComponent(unit.entityId(), unit.component1().reduceTime(delta));
               continue;
           }

           Entity entity;

           switch(unit.component1().type()) {
               case UnitInfo.BASE:
                   entity = EntityHelper.createMainBase(ecs, unit.component3().playerId(), unit.component3().uuid());
                   break;
               case UnitInfo.GENERATOR:
                   entity = EntityHelper.createGenerator(ecs, unit.component3().playerId(), unit.component3().uuid());
                   break;
               case UnitInfo.MINER:
                   entity = EntityHelper.createMiner(ecs, unit.component3().playerId(), unit.component3().uuid());
                   break;
               default:
                   continue;
           }

           entity.setComponent(unit.component2());
           ecs.delete(unit.entityId());
        }

    }

}
