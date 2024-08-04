package scenes.gamescene;

import core.EngineContext;
import core.ecs.Entity;
import core.ecs.components.Position;
import core.input.InputBuffer;
import core.util.Vector2f;
import core.util.Vector3f;
import scenes.lib.components.UnitInfo;
import scenes.lib.rendering.IsometricHelper;

import java.awt.event.KeyEvent;

public class CameraHandler {

    private Entity camera;
    private InputBuffer inputBuffer;

    public CameraHandler(EngineContext context, Entity cameraEntity) {
        camera = cameraEntity;
        inputBuffer = context.getService(InputBuffer.class);
    }

    public void update() {

        var movement = Vector2f.ZERO;

        if(inputBuffer.isKeyDown(KeyEvent.VK_W)) {
            movement = movement.add(0, 10);
        }
        if(inputBuffer.isKeyDown(KeyEvent.VK_S)) {
            movement = movement.add(0, -10);
        }
        if(inputBuffer.isKeyDown(KeyEvent.VK_A)) {
            movement = movement.add(10, 0);
        }
        if(inputBuffer.isKeyDown(KeyEvent.VK_D)) {
            movement = movement.add(-10, 0);
        }
        var position = camera.getComponent(Position.class);
        camera.setComponent(position.move(movement));

    }

    public void centerCameraOnMainBase(Position position) {
        var mainBaseOrigin = position.position().add(1f, 0, 1f);
        var cameraOffset = IsometricHelper.toScreenSpace(mainBaseOrigin).mul(-1);
        camera.setComponent(new Position(cameraOffset.toVector3f()));
    }

}
