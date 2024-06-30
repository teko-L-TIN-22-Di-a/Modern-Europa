package scenes.lib.rendering;

import core.util.Vector2f;

import java.awt.image.BufferedImage;

public record TextureAtlasEntry(BufferedImage image, Vector2f offset, Vector2f size) {
}
