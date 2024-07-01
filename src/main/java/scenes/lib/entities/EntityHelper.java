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
        newEntity.setComponent(new Sprite(TextureConstants.UNIT, Vector2f.of(7,18), true));
        newEntity.setComponent(new Visibility(playerId, 1));
        return newEntity;
    }

    public static Entity createMainBase(Ecs ecs, int playerId) {
        var newEntity = ecs.newEntity();
        newEntity.setComponent(new Sprite(TextureConstants.BASE, Vector2f.of(67,15), true));
        newEntity.setComponent(new Visibility(playerId, 8));
        return newEntity;
    }

}
