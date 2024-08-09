package scenes.gamescene;

import core.util.Vector2f;
import scenes.gamescene.rendering.MainGui;
import scenes.gamescene.rendering.SelectionRenderer;
import scenes.gamescene.rendering.IsometricTerrainRenderer;

public record RenderingContext(
        Vector2f scale,
        IsometricTerrainRenderer terrainRenderer,
        SelectionRenderer selectionRenderer,
        MainGui mainGui) {
}
