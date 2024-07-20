package scenes.lib.entities;

import core.ecs.Ecs;
import core.ecs.Entity;
import core.util.Vector2f;
import scenes.lib.TextureConstants;
import scenes.lib.components.Sprite;
import scenes.lib.components.Visibility;

public class EntityHelper {

    public static Entity createUnit(Ecs ecs, int playerId) {
        var newEntity = ecs.newEntity();
        newEntity.setComponent(new Sprite(TextureConstants.UNIT + playerId, Vector2f.of(13,28), true));
        newEntity.setComponent(new Visibility(playerId, 4));
        return newEntity;
    }

    public static Entity createMainBase(Ecs ecs, int playerId) {
        var newEntity = ecs.newEntity();
        newEntity.setComponent(new Sprite(TextureConstants.BASE + playerId, Vector2f.of(66,14), true));
        newEntity.setComponent(new Visibility(playerId, 8));
        return newEntity;
    }

    public static Entity createGenerator(Ecs ecs, int playerId) {
        var newEntity = ecs.newEntity();
        newEntity.setComponent(new Sprite(TextureConstants.GENERATOR + playerId, Vector2f.of(66,14), true));
        newEntity.setComponent(new Visibility(playerId, 8));
        return newEntity;
    }

}
