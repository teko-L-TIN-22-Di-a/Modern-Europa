package rendering;

import core.util.Bounds;
import core.util.Vector2f;

import java.awt.image.BufferedImage;
import java.util.Map;

public record RenderedChunkEntry(
        BufferedImage image,
        BufferedImage mouseMap,
        Map<Integer, Vector2f> idMap,
        Vector2f originOffset,
        Bounds visualBounds) {
}
