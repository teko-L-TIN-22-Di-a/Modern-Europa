package scenes.lib;

import core.util.Vector2f;

import java.util.List;

public record MapInfo(String mapName, List<Vector2f> startPoints, List<Vector2f> mineSpots) {
}
