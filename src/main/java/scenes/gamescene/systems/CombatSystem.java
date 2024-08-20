
package scenes.gamescene.systems;

import core.EngineContext;
import core.Parameters;
import core.ecs.*;
import core.ecs.components.Position;
import core.util.CircleBounds;
import scenes.gamescene.commands.CommandConstants;
import scenes.lib.components.AttackParticle;
import scenes.lib.components.Combat;
import scenes.lib.components.Sprite;
import scenes.lib.components.UnitInfo;
import scenes.lib.helper.EntityHelper;

import java.util.List;
import java.util.Map;

import static scenes.lib.config.RenderingConfig.FRAME_RATE;

public class CombatSystem implements RunnableSystem {

    private final Ecs ecs;
    private final int playerId;

    public CombatSystem(EngineContext context, int playerId) {
        ecs = context.getService(Ecs.class);
        this.playerId = playerId;
    }

    public void update(double delta) {
        var combatUnits = ecs
                .view(Position.class, Combat.class, UnitInfo.class)
                .stream().filter(entry -> entry.component3().playerId() == playerId)
                .toList();

        var enemyUnits = ecs
                .view(Position.class, UnitInfo.class)
                .stream().filter(entry -> entry.component2().playerId() != playerId)
                .toList();

        for(var unit : combatUnits) {

            if(!unit.component2().ready()) {
                ecs.setComponent(unit.entityId(), unit.component2().cooldown(delta));
                continue;
            }

            if(TryAttacking(unit, enemyUnits)) {
                continue;
            }

        }
    }

    public boolean TryAttacking(EcsView3<Position, Combat, UnitInfo> unit, List<EcsView2<Position, UnitInfo>> enemyUnits) {
        var unitBounds = new CircleBounds(
                unit.component1().position().toVector2fxz(),
                unit.component3().visibilityStrength());

        for(var enemyUnit : enemyUnits) {
            if(!unitBounds.intersects(enemyUnit.component1().position().toVector2fxz())) {
                continue;
            }

            EntityHelper.createCommand(ecs, CommandConstants.AUTO_ATTACK, new Parameters(Map.ofEntries(
                    Map.entry(CommandConstants.AUTO_ATTACK_ORIGIN, unit.component1().position()),
                    Map.entry(CommandConstants.AUTO_ATTACK_TARGET_ENTITY_ID, enemyUnit.component2().uuid()),
                    Map.entry(CommandConstants.AUTO_ATTACK_TARGET_DAMAGE, unit.component2().damage())
            )));

            var combatComponent = unit.component2();
            ecs.setComponent(unit.entityId(), combatComponent.attack(combatComponent.speed() * FRAME_RATE));

            return true;
        }

        return false;
    }

}
