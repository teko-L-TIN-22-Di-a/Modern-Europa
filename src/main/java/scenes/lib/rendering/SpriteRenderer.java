package scenes.lib.rendering;

import core.EngineContext;
import core.ecs.Ecs;
import core.ecs.EcsView2;
import core.ecs.components.Camera;
import core.ecs.components.Position;
import core.ecs.helper.CameraHelper;
import core.util.Vector2f;
import scenes.lib.components.Sprite;

import java.awt.*;
import java.util.List;

public class SpriteRenderer implements Renderer {

    private final Ecs ecs;
    private TextureAtlas textureAtlas;

    public SpriteRenderer(EngineContext context, TextureAtlas textureAtlas) {
        ecs = context.getService(Ecs.class);
        this.textureAtlas = textureAtlas;
    }

    @Override
    public void render(Graphics2D g2d) {
        var cameraEntries = ecs.view(Camera.class, Position.class);
        var cameraOffset = getCameraOffset(cameraEntries);

        var spriteEntries = ecs.view(Sprite.class, Position.class);
        for (var spriteEntry : spriteEntries) {
            if(spriteEntry.component1().visible()) continue;

            g2d.setColor(Color.RED);

            var spritePos = IsometricHelper.toScreenSpace(spriteEntry.component2().position());
            var pos = spritePos.add(cameraOffset);

            g2d.fillRect((int) pos.x(), (int) pos.y(), 8, 8);
        }

    }

    private Vector2f getCameraOffset(List<EcsView2<Camera, Position>> cameras) {
        for (var entry : cameras) {
            if(entry.component1().active()) {
                return CameraHelper.GetCameraOffset(entry.component1(), entry.component2());
            }
        }

        return Vector2f.ZERO;
    }

}
