package core.ecs.helper;

import core.ecs.components.CameraComponent;
import core.ecs.components.PositionComponent;
import core.util.Vector2f;

public class CameraHelper {

    public static Vector2f GetCameraOffset(CameraComponent camera, PositionComponent position) {
        return position.position().add(camera.viewPort().div(Vector2f.of(2)));
    }

}
