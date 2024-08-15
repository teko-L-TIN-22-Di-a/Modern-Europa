package scenes.lib.rendering;

import core.util.Vector2f;

import java.awt.*;

public interface Renderer {

    void render(Graphics2D g2d);

    void setScale(Vector2f scale);

}
