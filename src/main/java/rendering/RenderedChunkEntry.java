package rendering;

import core.util.Vector2f;

import java.awt.image.BufferedImage;

public record RenderedChunkEntry(BufferedImage image, Vector2f coordinateOrigin) {
}
