package core.ecs.helper;

import core.ecs.components.Camera;
import core.ecs.components.Position;
import core.util.Vector2f;

public class CameraHelper {

    public static Vector2f GetCameraOffset(Camera camera, Position position) {
        return position.position().add(camera.viewPort().div(Vector2f.of(2)));
    }

}
