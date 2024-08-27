package scenes.gamescene.systems;

import core.EngineContext;
import core.Parameters;
import core.ecs.Ecs;
import core.ecs.RunnableSystem;
import core.ecs.components.Position;
import scenes.gamescene.commands.CommandConstants;
import scenes.lib.components.Combat;
import scenes.lib.components.Mining;
import scenes.lib.components.Powered;
import scenes.lib.components.UnitInfo;
import scenes.lib.helper.EntityHelper;

import java.util.Map;

import static scenes.lib.config.RenderingConfig.FRAME_RATE;

public class MiningSystem implements RunnableSystem {

    private final Ecs ecs;
    private final int playerId;

    public MiningSystem(EngineContext context, int playerId) {
        ecs = context.getService(Ecs.class);
        this.playerId = playerId;
    }

    @Override
    public void update(double delta) {
        var miners = ecs
                .view(Mining.class, UnitInfo.class, Powered.class)
                .stream().filter(entry -> entry.component2().playerId() == playerId)
                .toList();

        for(var miner : miners) {

            if(!miner.component3().powered()) {
                continue;
            }

            if(!miner.component1().ready()) {
                ecs.setComponent(miner.entityId(), miner.component1().cooldown(delta));
                continue;
            }

            EntityHelper.createCommand(ecs, CommandConstants.MINING, new Parameters(Map.ofEntries(
                    Map.entry(CommandConstants.MINING_AMOUNT, miner.component1().gain()),
                    Map.entry(CommandConstants.MINING_PLAYER_ID, playerId)
            )));

            ecs.setComponent(miner.entityId(), miner.component1().mine(miner.component1().speed() * FRAME_RATE));

        }
    }

}
