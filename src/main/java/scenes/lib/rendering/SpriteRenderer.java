package scenes.lib.rendering;

import core.EngineContext;
import core.ecs.Ecs;
import core.ecs.components.Position;
import scenes.lib.helper.CameraHelper;
import core.util.Vector2f;
import scenes.gamescene.rendering.IsometricHelper;
import scenes.lib.components.Sprite;

import java.awt.*;
import java.util.Comparator;

public class SpriteRenderer implements Renderer {

    private final Ecs ecs;
    private TextureAtlas textureAtlas;

    public SpriteRenderer(EngineContext context, TextureAtlas textureAtlas) {
        ecs = context.getService(Ecs.class);
        this.textureAtlas = textureAtlas;
    }

    @Override
    public void render(Graphics2D g2d) {
        var cameraOffset = CameraHelper.getCameraOffset(ecs);

        var spriteEntries = ecs.view(Sprite.class, Position.class);
        // Depth sort
        spriteEntries.sort(Comparator.comparing(entry -> {
            var pos = entry.component2().position();
            return pos.x() + pos.z() + pos.y();
        }));

        for (var spriteEntry : spriteEntries) {
            if(!spriteEntry.component1().visible()) continue;

            var spritePos = IsometricHelper.toScreenSpace(spriteEntry.component2().position());
            var drawingPos = spritePos.add(cameraOffset).sub(spriteEntry.component1().origin());

            var texture = textureAtlas.get(spriteEntry.component1().resourcePath());

            if(texture == null || texture.image() == null) {
                System.out.println(spriteEntry.component1().resourcePath());
            }

            g2d.drawImage(
                    texture.image(),
                    (int) drawingPos.x(),
                    (int) drawingPos.y(),
                    (int) (drawingPos.x() + texture.size().x()),
                    (int) (drawingPos.y() + texture.size().y()),
                    (int) texture.offset().x(), (int) texture.offset().y(),
                    (int) (texture.offset().x() + texture.size().x()),
                    (int) (texture.offset().y() + texture.size().y()),
                    null
            );

            //g2d.fillRect((int) drawingPos.x(), (int) drawingPos.y(), 8, 8);
        }

    }

    @Override
    public void setSize(Vector2f size) {
        // Do nothing
    }

    @Override
    public void setScale(Vector2f scale) {
        // Do nothing
    }

}
