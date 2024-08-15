package scenes.gamescene;

import config.ScreenConfig;
import config.WindowConfig;
import core.EngineContext;
import core.ecs.Entity;
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
    private static float MAX_ZOOM = 1.6f;

    private RenderingContext renderingContext;
    private Entity camera;
    private InputBuffer inputBuffer;
    private MouseListener mouseListener;
    private float currentZoom;

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

        while (!queuedMouseWheelEvents.isEmpty()) {
            var nextMovement = queuedMouseWheelEvents.poll();

            currentZoom = Math.min(Math.max(currentZoom + (nextMovement.floatValue() * 0.1f), MIN_ZOOM), MAX_ZOOM);

            var newScale = ScreenConfig.ViewportSize.mul(currentZoom);

            renderingContext.bufferedRenderer().resize(newScale);
        }

    }

    public void centerCameraOnMainBase(Position position) {
        var mainBaseOrigin = position.position().add(1f, 0, 1f);
        var cameraOffset = IsometricHelper.toScreenSpace(mainBaseOrigin).mul(-1);
        camera.setComponent(new Position(cameraOffset.toVector3f()));
    }

}
