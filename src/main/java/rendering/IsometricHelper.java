package rendering;

import core.util.Vector2f;
import core.util.Vector3f;

public class IsometricHelper {

    public final static Vector2f TileSize = Vector2f.of(62, 30);
    public final static Vector2f HalfTileSize = Vector2f.of(31, 15);

    public static Vector2f toScreenSpace(Vector2f pos) {
        return toScreenSpace(pos.x(), 0, pos.y());
    }

    public static Vector2f toScreenSpace(Vector3f position) {
        return toScreenSpace(position.x(), position.y(), position.z());
    }

    public static Vector2f toScreenSpace(float x, float y, float z) {
        var xPart = Vector2f.of(HalfTileSize.x() * x, HalfTileSize.y() * x);
        var zPart = Vector2f.of(-HalfTileSize.x() * z, HalfTileSize.y() * z);
        var yPart = Vector2f.of(0, HalfTileSize.y() * y);

        return xPart.add(zPart).add(yPart);
    }

}
