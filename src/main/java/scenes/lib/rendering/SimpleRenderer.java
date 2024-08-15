package scenes.lib.rendering;

import core.util.Vector2f;
import rx.functions.Action2;

import java.awt.*;

public class SimpleRenderer implements Renderer {

    private Action2<Graphics2D, Vector2f> renderAction;
    private Vector2f scale = Vector2f.of(1,1);

    public SimpleRenderer(Action2<Graphics2D, Vector2f> renderAction) {
        this.renderAction = renderAction;
    }

    @Override
    public void render(Graphics2D g2d) {
        renderAction.call(g2d, scale);
    }

    @Override
    public void setScale(Vector2f scale) {
        this.scale = scale;
    }
}
