package scenes.lib.rendering;

import core.util.Vector2f;
import rx.functions.Action2;
import rx.functions.Action3;

import java.awt.*;

public class SimpleRenderer implements Renderer {

    private final Action3<Graphics2D, Vector2f, Vector2f> renderAction;
    private Vector2f scale = Vector2f.of(1);
    private Vector2f size = Vector2f.of(1);

    public SimpleRenderer(Action3<Graphics2D, Vector2f, Vector2f> renderAction) {
        this.renderAction = renderAction;
    }

    @Override
    public void render(Graphics2D g2d) {
        renderAction.call(g2d, scale, size);
    }

    @Override
    public void setSize(Vector2f size) {
        this.size = size;
    }

    @Override
    public void setScale(Vector2f scale) {
        this.scale = scale;
    }
}
