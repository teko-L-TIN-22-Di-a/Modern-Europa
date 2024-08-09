package scenes.gamescene.rendering;

import core.util.Vector2f;

import java.awt.image.BufferedImage;

public record FogOfWarChunkEntry(
        BufferedImage image,
        Vector2f originOffset) {
}
