package scenes.lib.components;

public record UnitInfo(int playerId, int visibilityStrength, int movementSpeed, String type, String uuid) {

    public static final String CONSTRUCTION_SITE = "constructionSite";

    public static final String GENERATOR = "generator";
    public static final String MINER = "miner";
    public static final String BASE = "base";

    public static final String MECH_UNIT = "mechUnit";
    public static final String BALL_UNIT = "ballUnit";

}