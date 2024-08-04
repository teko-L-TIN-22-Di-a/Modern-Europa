package scenes.lib.components;

import core.util.Bounds;

public record Selection(Bounds bounds, boolean selected) {
    public Selection unselect() {
        return new Selection(bounds(), false);
    }
    public Selection select() {
        return new Selection(bounds(), true);
    }
}
