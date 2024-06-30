package core.util;

public record Vector2f(float x, float y) {

    public static Vector2f ZERO = new Vector2f(0f, 0f);

    public static Vector2f of(float val) {
        return new Vector2f(val, val);
    }
    public static Vector2f of(float x, float y) {
        return new Vector2f(x, y);
    }

    public Vector3f toVector3fz(float z) {
        return new Vector3f(x, y, z);
    }

    public Vector3f toVector3fy(float y) {
        return new Vector3f(x, y, this.y);
    }

    public Vector3f toVector3f() {
        return new Vector3f(x, y, 0);
    }

    public Vector2f add(float x, float y) {
        return new Vector2f(this.x + x, this.y + y);
    }
    public Vector2f add(Vector2f v) {
        return new Vector2f(x + v.x, y + v.y);
    }

    public Vector2f sub(float x, float y) {
        return new Vector2f(this.x - x, this.y - y);
    }
    public Vector2f sub(Vector2f v) {
        return new Vector2f(x - v.x, y - v.y);
    }

    public Vector2f mul(Vector2f v) {
        return new Vector2f(x * v.x, y * v.y);
    }

    public Vector2f div(Vector2f v) {
        return new Vector2f(x / v.x, y / v.y);
    }

    public Vector2f div(float x, float y) {
        return new Vector2f(this.x / x, this.y / y);
    }

}
