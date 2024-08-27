package scenes.gamescene;

import core.util.Vector2f;
import scenes.gamescene.rendering.gui.MainGui;
import scenes.gamescene.rendering.SelectionRenderer;
import scenes.gamescene.rendering.IsometricTerrainRenderer;
import scenes.lib.rendering.BufferedRenderer;

public record RenderingContext(
        BufferedRenderer bufferedRenderer,
        IsometricTerrainRenderer terrainRenderer,
        SelectionRenderer selectionRenderer,
        MainGui mainGui) {

    public Vector2f getScale() {
        return bufferedRenderer.getScale();
    }

}
