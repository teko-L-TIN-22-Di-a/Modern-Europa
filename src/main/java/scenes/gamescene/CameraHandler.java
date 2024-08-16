package scenes.gamescene;

import scenes.lib.config.ScreenConfig;
import core.EngineContext;
import core.ecs.Entity;
import core.ecs.components.Camera;
import core.ecs.components.Position;
import core.input.InputBuffer;
import core.input.MouseListener;
import core.util.Vector2f;
import scenes.gamescene.rendering.IsometricHelper;

import java.awt.event.KeyEvent;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CameraHandler {

    private final Queue<Double> queuedMouseWheelEvents = new ConcurrentLinkedQueue<>();

    private static float MIN_ZOOM = 0.6f;
    private static float MAX_ZOOM = 2f;

    private static float CAMERA_SPEED = 10;

    private RenderingContext renderingContext;
    private Entity camera;
    private InputBuffer inputBuffer;
    private MouseListener mouseListener;
    private float currentZoom = 1;

    public CameraHandler(EngineContext context, RenderingContext renderingContext, Entity cameraEntity) {
        this.renderingContext = renderingContext;
        camera = cameraEntity;
        inputBuffer = context.getService(InputBuffer.class);
        mouseListener = context.getService(MouseListener.class);

        mouseListener.bindMouseWheelMoved(e -> {
           queuedMouseWheelEvents.add(e.getPreciseWheelRotation());
        });

    }

    public void update() {

        var movement = Vector2f.ZERO;

        if(inputBuffer.isKeyDown(KeyEvent.VK_W)) {
            movement = movement.add(0, CAMERA_SPEED);
        }
        if(inputBuffer.isKeyDown(KeyEvent.VK_S)) {
            movement = movement.add(0, -CAMERA_SPEED);
        }
        if(inputBuffer.isKeyDown(KeyEvent.VK_A)) {
            movement = movement.add(CAMERA_SPEED, 0);
        }
        if(inputBuffer.isKeyDown(KeyEvent.VK_D)) {
            movement = movement.add(-CAMERA_SPEED, 0);
        }
        var position = camera.getComponent(Position.class);
        camera.setComponent(position.move(movement.mul(currentZoom)));

        while (!queuedMouseWheelEvents.isEmpty()) {
            var nextMovement = queuedMouseWheelEvents.poll();

            currentZoom = Math.min(Math.max(currentZoom + (nextMovement.floatValue() * 0.1f), MIN_ZOOM), MAX_ZOOM);

            var newSize = ScreenConfig.VIEWPORT_SIZE.mul(currentZoom);

            camera.setComponent(new Camera(newSize, true));
            renderingContext.bufferedRenderer().resize(newSize);
        }

    }

    public void centerCameraOnMainBase(Position position) {
        var mainBaseOrigin = position.position().add(1f, 0, 1f);
        var cameraOffset = IsometricHelper.toScreenSpace(mainBaseOrigin).mul(-1);
        camera.setComponent(new Position(cameraOffset.toVector3f()));
    }

}
