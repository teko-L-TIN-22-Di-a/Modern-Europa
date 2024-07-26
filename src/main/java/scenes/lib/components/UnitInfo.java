package scenes.lib.components;

public record UnitInfo(int playerId, String type) {
    public static final String GENERATOR = "generator";
    public static final String BASE = "base";
    public static final String MECH_UNIT = "mech_unit";
}