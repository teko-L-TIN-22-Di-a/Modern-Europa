package scenes.lib.entities;

import core.ecs.Ecs;
import core.ecs.Entity;
import core.util.Vector2f;
import scenes.lib.TextureConstants;
import scenes.lib.components.Sprite;
import scenes.lib.components.UnitInfo;
import scenes.lib.components.Visibility;

public class EntityHelper {

    public static Entity createUnit(Ecs ecs, int playerId) {
        var newEntity = ecs.newEntity();
        newEntity.setComponent(new Sprite(TextureConstants.UNIT + playerId, Vector2f.of(13,28), true));
        newEntity.setComponent(new Visibility(4));
        newEntity.setComponent(new UnitInfo(playerId, UnitInfo.MECH_UNIT));
        return newEntity;
    }

    public static Entity createMainBase(Ecs ecs, int playerId) {
        var newEntity = ecs.newEntity();
        newEntity.setComponent(new Sprite(TextureConstants.BASE + playerId, Vector2f.of(66,14), true));
        newEntity.setComponent(new Visibility(8));
        newEntity.setComponent(new UnitInfo(playerId, UnitInfo.BASE));
        return newEntity;
    }

    public static Entity createGenerator(Ecs ecs, int playerId) {
        var newEntity = ecs.newEntity();
        newEntity.setComponent(new Sprite(TextureConstants.GENERATOR + playerId, Vector2f.of(66,14), true));
        newEntity.setComponent(new Visibility(8));
        newEntity.setComponent(new UnitInfo(playerId, UnitInfo.GENERATOR));
        return newEntity;
    }

}
