package scenes.lib.entities;

import core.ecs.Ecs;
import core.ecs.Entity;
import core.util.Bounds;
import core.util.Vector2f;
import scenes.lib.TextureConstants;
import scenes.lib.components.Selection;
import scenes.lib.components.Sprite;
import scenes.lib.components.UnitInfo;

public class EntityHelper {

    public static Entity createUnit(Ecs ecs, int playerId) {
        var newEntity = ecs.newEntity();
        var originOffset = Vector2f.of(13,28);

        newEntity.setComponent(new Sprite(TextureConstants.UNIT + playerId, originOffset, true));
        newEntity.setComponent(new UnitInfo(playerId, 4, UnitInfo.MECH_UNIT));
        newEntity.setComponent(new Selection(new Bounds(originOffset.mul(-1), Vector2f.of(26, 28)), false));
        return newEntity;
    }

    public static Entity createMainBase(Ecs ecs, int playerId) {
        var newEntity = ecs.newEntity();
        var originOffset = Vector2f.of(66,14);

        newEntity.setComponent(new Sprite(TextureConstants.BASE + playerId, originOffset, true));
        newEntity.setComponent(new UnitInfo(playerId, 8, UnitInfo.BASE));
        newEntity.setComponent(new Selection(new Bounds(originOffset.mul(-1).add(10, 8), Vector2f.of(102, 62)), false));
        return newEntity;
    }

    public static Entity createGenerator(Ecs ecs, int playerId) {
        var newEntity = ecs.newEntity();
        var originOffset = Vector2f.of(66,14);

        newEntity.setComponent(new Sprite(TextureConstants.GENERATOR + playerId, originOffset, true));
        newEntity.setComponent(new UnitInfo(playerId, 8, UnitInfo.GENERATOR));
        newEntity.setComponent(new Selection(new Bounds(originOffset.mul(-1).add(10, 8), Vector2f.of(102, 62)), false));
        return newEntity;
    }

}
