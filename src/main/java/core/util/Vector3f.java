package core.util;

public record Vector3f(float x, float y, float z) {

    public static Vector3f ZERO = new Vector3f(0f, 0f, 0f);

    public Vector3f add(Vector3f v) {
        return new Vector3f(x + v.x, y + v.y, z + v.z);
    }

    public Vector3f sub(Vector3f v) {
        return new Vector3f(x - v.x, y - v.y, z - v.z);
    }

    public Vector3f mul(Vector3f v) {
        return new Vector3f(x * v.x, y * v.y, z * v.z);
    }

    public Vector3f div(Vector3f v) {
        return new Vector3f(x / v.x, y / v.y, z / v.z);
    }

}
