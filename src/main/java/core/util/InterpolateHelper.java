package core.util;

public class InterpolateHelper {

    public static Vector2f interpolateLinear(Vector2f from, Vector2f to, float speed) {
        return Vector2f.of(
                interpolateLinear(from.x(), to.x(), speed),
                interpolateLinear(from.y(), to.y(), speed));
    }

    public static float interpolateLinear(float from, float to, float speed) {
        return from * (1 - speed) + to * speed;
    }

}
