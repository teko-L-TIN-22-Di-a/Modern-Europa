package scenes.lib.helper;

import core.util.Vector2f;
import scenes.lib.MapInfo;

import java.util.Arrays;

public class MapHelper {

    public static MapInfo getDefaultMap() {

        var startSpots = Arrays.asList(
                Vector2f.of(1, 1),
                Vector2f.of(23, 23),
                Vector2f.of(23, 1),
                Vector2f.of(1, 23)
        );
        var mineSpots = Arrays.asList(
                Vector2f.of(8,3),
                Vector2f.of(3,8),
                Vector2f.of(9,9),

                Vector2f.of(12,12),

                Vector2f.of(16,3),
                Vector2f.of(21,8),
                Vector2f.of(15,9),

                Vector2f.of(3,16),
                Vector2f.of(9,15),
                Vector2f.of(8,21),

                Vector2f.of(15,15),
                Vector2f.of(21,16),
                Vector2f.of(16,21)
        );

        return new MapInfo("Default", startSpots, mineSpots);
    }

}
