package scenes.lib.helper;

import scenes.lib.AnimationConstants;
import scenes.lib.config.RenderingConfig;
import core.Parameters;
import core.ecs.Ecs;
import core.ecs.Entity;
import core.util.Bounds;
import core.util.Vector2f;
import scenes.lib.TextureConstants;
import scenes.lib.components.*;

import java.util.UUID;

public class EntityHelper {

    public static Entity createCommand(Ecs ecs, String commandType, Parameters parameters) {
        var newEntity = ecs.newEntity();
        newEntity.setComponent(Command.create(commandType, parameters));

        return newEntity;
    }

    public static Entity createBallUnit(Ecs ecs, int playerId) {
        return createBallUnit(ecs, playerId, UUID.randomUUID().toString());
    }
    public static Entity createBallUnit(Ecs ecs, int playerId, String uuid) {
        var newEntity = ecs.newEntity();
        var originOffset = RenderingConfig.COMBAT_UNIT_SIZE.mul(Vector2f.of(0.5f, 1));

        newEntity.setComponent(new Sprite(TextureConstants.BALL_UNIT + playerId, originOffset, true));
        newEntity.setComponent(new UnitInfo(playerId, 2, 100, UnitInfo.BALL_UNIT, 100, uuid));
        newEntity.setComponent(new Selection(new Bounds(originOffset.mul(-1), Vector2f.of(26, 28)), false));
        newEntity.setComponent(SpriteAnimation.of(AnimationConstants.BALL_UNIT_ANIMATIONS + playerId));
        newEntity.setComponent(Combat.of(10, 2));
        return newEntity;
    }

    public static Entity createMechUnit(Ecs ecs, int playerId) {
        return createMechUnit(ecs, playerId, UUID.randomUUID().toString());
    }
    public static Entity createMechUnit(Ecs ecs, int playerId, String uuid) {
        var newEntity = ecs.newEntity();
        var originOffset = RenderingConfig.COMBAT_UNIT_SIZE.mul(Vector2f.of(0.5f, 1));

        newEntity.setComponent(new Sprite(TextureConstants.MECH_UNIT + playerId, originOffset, true));
        newEntity.setComponent(new UnitInfo(playerId, 2, 10, UnitInfo.MECH_UNIT, 100, uuid));
        newEntity.setComponent(new Selection(new Bounds(originOffset.mul(-1), Vector2f.of(26, 28)), false));
        newEntity.setComponent(SpriteAnimation.of(AnimationConstants.MECH_UNIT_ANIMATIONS + playerId));
        newEntity.setComponent(Combat.of(50, 5));

        return newEntity;
    }

    public static Entity createMainBase(Ecs ecs, int playerId) {
        return createMainBase(ecs, playerId, UUID.randomUUID().toString());
    }
    public static Entity createMainBase(Ecs ecs, int playerId, String uuid) {
        var newEntity = ecs.newEntity();
        var originOffset = Vector2f.of(66,48);

        newEntity.setComponent(new Sprite(TextureConstants.BASE + playerId, originOffset, true));
        newEntity.setComponent(new UnitInfo(playerId, 3, 0, UnitInfo.BASE, 100, uuid));
        newEntity.setComponent(new Selection(new Bounds(originOffset.mul(-1).add(10, 8), Vector2f.of(102, 62)), false));
        newEntity.setComponent(Powered.off());
        return newEntity;
    }

    public static Entity createGenerator(Ecs ecs, int playerId) {
        return createGenerator(ecs, playerId, UUID.randomUUID().toString());
    }
    public static Entity createGenerator(Ecs ecs, int playerId, String uuid) {
        var newEntity = ecs.newEntity();
        var originOffset = Vector2f.of(66,48);

        newEntity.setComponent(new Sprite(TextureConstants.GENERATOR + playerId, originOffset, true));
        newEntity.setComponent(new UnitInfo(playerId, 2, 0, UnitInfo.GENERATOR,100, uuid));
        newEntity.setComponent(new Selection(new Bounds(originOffset.mul(-1).add(10, 8), Vector2f.of(102, 62)), false));
        newEntity.setComponent(new Generator(3));
        return newEntity;
    }

    public static Entity createMiner(Ecs ecs, int playerId) {
        return createMiner(ecs, playerId, UUID.randomUUID().toString());
    }
    public static Entity createMiner(Ecs ecs, int playerId, String uuid) {
        var newEntity = ecs.newEntity();
        var originOffset = Vector2f.of(66,48);

        newEntity.setComponent(new Sprite(TextureConstants.MINER + playerId, originOffset, true));
        newEntity.setComponent(new UnitInfo(playerId, 2, 0, UnitInfo.MINER,100, uuid));
        newEntity.setComponent(new Selection(new Bounds(originOffset.mul(-1).add(10, 8), Vector2f.of(102, 62)), false));
        newEntity.setComponent(Powered.off());
        return newEntity;
    }

    public static Entity createConstructionSite(Ecs ecs, String buildingType, int playerId) {
        return createConstructionSite(ecs, playerId, buildingType,  UUID.randomUUID().toString());
    }
    public static Entity createConstructionSite(Ecs ecs, int playerId, String buildingType, String uuid) {
        var newEntity = ecs.newEntity();
        var originOffset = Vector2f.of(66,48);

        newEntity.setComponent(new Sprite(TextureConstants.CONSTRUCTION_SITE + playerId, originOffset, true));
        newEntity.setComponent(new UnitInfo(playerId, 2, 0, UnitInfo.CONSTRUCTION_SITE,100, uuid));
        newEntity.setComponent(new Selection(new Bounds(originOffset.mul(-1).add(10, 8), Vector2f.of(102, 62)), false));
        newEntity.setComponent(new Construction(buildingType, 5 * 60));
        return newEntity;
    }

}
