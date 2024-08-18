package scenes.lib.components;

import core.util.Vector2f;

public record Sprite(String resourcePath, Vector2f origin, boolean visible) {

    public Sprite usingResource(String newResourcePath) {
        return new Sprite(newResourcePath, origin, visible);
    }

}
