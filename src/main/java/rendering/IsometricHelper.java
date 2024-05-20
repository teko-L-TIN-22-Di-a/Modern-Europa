package rendering;

import config.TileConfig;
import core.util.Vector2f;
import core.util.Vector3f;

public class IsometricHelper {

    public static Vector2f toScreenSpace(Vector2f pos) {
        return toScreenSpace(pos.x(), 0, pos.y());
    }

    public static Vector2f toScreenSpace(Vector3f position) {
        return toScreenSpace(position.x(), position.y(), position.z());
    }

    public static Vector2f toScreenSpace(float x, float y, float z) {
        var xPart = Vector2f.of(TileConfig.HalfTileSize.x() * x, TileConfig.HalfTileSize.y() * x);
        var zPart = Vector2f.of(-TileConfig.HalfTileSize.x() * z, TileConfig.HalfTileSize.y() * z);
        var yPart = Vector2f.of(0, TileConfig.HalfTileSize.y() * y);

        return xPart.add(zPart).add(yPart);
    }

}
