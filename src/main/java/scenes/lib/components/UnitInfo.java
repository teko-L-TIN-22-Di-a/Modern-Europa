package scenes.lib.components;

public record UnitInfo(int playerId, int visibilityStrength, String type) {
    public static final String GENERATOR = "generator";
    public static final String BASE = "base";
    public static final String MECH_UNIT = "mech_unit";
}