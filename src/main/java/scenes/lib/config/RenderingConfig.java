package scenes.lib.config;

import core.util.Vector2f;

public class RenderingConfig {
    public final static Vector2f TILE_SIZE = Vector2f.of(132, 68);
    public final static Vector2f HALF_TILE_SIZE = TILE_SIZE.div(Vector2f.of(2));

    public final static Vector2f COMBAT_UNIT_SIZE = Vector2f.of(26, 28);
    public final static Vector2f BUILDING_SIZE = Vector2f.of(132, 82);

}
