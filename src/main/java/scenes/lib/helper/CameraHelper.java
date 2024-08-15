package scenes.lib.helper;

import core.ecs.Ecs;
import core.ecs.EcsView2;
import core.ecs.Entity;
import core.ecs.components.Camera;
import core.ecs.components.Position;
import core.util.Vector2f;

import java.util.List;

public class CameraHelper {

    public static Vector2f getCameraOffset(Camera camera, Position position) {
        return position
                .position()
                .toVector2fxy()
                .add(camera.viewPort().div(Vector2f.of(2)));
    }

    public static Vector2f getCameraOffset(Entity cameraEntity) {
        var camera = cameraEntity.getComponent(Camera.class);
        var position = cameraEntity.getComponent(Position.class);

        return getCameraOffset(camera, position);
    }

    public static Vector2f getCameraOffset(List<EcsView2<Camera, Position>> cameras) {
        for (var entry : cameras) {
            if(entry.component1().active()) {
                return getCameraOffset(entry.component1(), entry.component2());
            }
        }

        return Vector2f.ZERO;
    }

    public static Vector2f getCameraOffset(Ecs ecs) {
        var cameraEntries = ecs.view(Camera.class, Position.class);
        return getCameraOffset(cameraEntries);
    }

}
