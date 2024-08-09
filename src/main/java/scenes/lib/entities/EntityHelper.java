package scenes.lib.entities;

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

    public static Entity createSmallUnit(Ecs ecs, int playerId) {
        return createSmallUnit(ecs, playerId, UUID.randomUUID().toString());
    }
    public static Entity createSmallUnit(Ecs ecs, int playerId, String uuid) {
        var newEntity = ecs.newEntity();
        var originOffset = Vector2f.of(13,28);

        newEntity.setComponent(new Sprite(TextureConstants.SMALL_UNIT + playerId, originOffset, true));
        newEntity.setComponent(new UnitInfo(playerId, 10, 100, UnitInfo.BALL_UNIT, uuid));
        newEntity.setComponent(new Selection(new Bounds(originOffset.mul(-1), Vector2f.of(26, 28)), false));
        return newEntity;
    }

    public static Entity createUnit(Ecs ecs, int playerId) {
        return createUnit(ecs, playerId, UUID.randomUUID().toString());
    }
    public static Entity createUnit(Ecs ecs, int playerId, String uuid) {
        var newEntity = ecs.newEntity();
        var originOffset = Vector2f.of(13,28);

        newEntity.setComponent(new Sprite(TextureConstants.UNIT + playerId, originOffset, true));
        newEntity.setComponent(new UnitInfo(playerId, 16, 10, UnitInfo.MECH_UNIT, uuid));
        newEntity.setComponent(new Selection(new Bounds(originOffset.mul(-1), Vector2f.of(26, 28)), false));
        return newEntity;
    }

    public static Entity createMainBase(Ecs ecs, int playerId) {
        return createMainBase(ecs, playerId, UUID.randomUUID().toString());
    }
    public static Entity createMainBase(Ecs ecs, int playerId, String uuid) {
        var newEntity = ecs.newEntity();
        var originOffset = Vector2f.of(65,48);

        newEntity.setComponent(new Sprite(TextureConstants.BASE + playerId, originOffset, true));
        newEntity.setComponent(new UnitInfo(playerId, 8, 0, UnitInfo.BASE, uuid));
        newEntity.setComponent(new Selection(new Bounds(originOffset.mul(-1).add(10, 8), Vector2f.of(102, 62)), false));
        return newEntity;
    }

    public static Entity createGenerator(Ecs ecs, int playerId) {
        return createGenerator(ecs, playerId, UUID.randomUUID().toString());
    }
    public static Entity createGenerator(Ecs ecs, int playerId, String uuid) {
        var newEntity = ecs.newEntity();
        var originOffset = Vector2f.of(65,48);

        newEntity.setComponent(new Sprite(TextureConstants.GENERATOR + playerId, originOffset, true));
        newEntity.setComponent(new UnitInfo(playerId, 8, 0, UnitInfo.GENERATOR, uuid));
        newEntity.setComponent(new Selection(new Bounds(originOffset.mul(-1).add(10, 8), Vector2f.of(102, 62)), false));
        return newEntity;
    }

    public static Entity createMiner(Ecs ecs, int playerId) {
        return createMiner(ecs, playerId, UUID.randomUUID().toString());
    }
    public static Entity createMiner(Ecs ecs, int playerId, String uuid) {
        var newEntity = ecs.newEntity();
        var originOffset = Vector2f.of(63,48);

        newEntity.setComponent(new Sprite(TextureConstants.MINER + playerId, originOffset, true));
        newEntity.setComponent(new UnitInfo(playerId, 8, 0, UnitInfo.MINER, uuid));
        newEntity.setComponent(new Selection(new Bounds(originOffset.mul(-1).add(10, 8), Vector2f.of(102, 62)), false));
        return newEntity;
    }

    public static Entity createConstructionSite(Ecs ecs, String buildingType, int playerId) {
        return createConstructionSite(ecs, playerId, buildingType,  UUID.randomUUID().toString());
    }
    public static Entity createConstructionSite(Ecs ecs, int playerId, String buildingType, String uuid) {
        var newEntity = ecs.newEntity();
        var originOffset = Vector2f.of(63,48);

        newEntity.setComponent(new Sprite(TextureConstants.CONSTRUCTION_SITE + playerId, originOffset, true));
        newEntity.setComponent(new UnitInfo(playerId, 8, 0, UnitInfo.CONSTRUCTION_SITE, uuid));
        newEntity.setComponent(new Selection(new Bounds(originOffset.mul(-1).add(10, 8), Vector2f.of(102, 62)), false));
        newEntity.setComponent(new Construction(buildingType, 5 * 60));
        return newEntity;
    }

}
