package scenes.gamescene;

import scenes.gamescene.rendering.SelectionRenderer;
import scenes.lib.gui.MainGui;

public record RenderingContext(SelectionRenderer selectionRenderer, MainGui mainGui) {
}
