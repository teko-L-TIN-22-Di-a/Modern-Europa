package core.util;

public record Bounds(Vector2f position, Vector2f size) {

    public boolean intersects(Vector2f point) {
        return (point.x() < position.x() + size.x() && point.y() < position.y() + size.y())
                && (point.x() > position.x() && point.y() > position.y());
    }

    public boolean intersects(Bounds bounds) {
        return (bounds.position.x() < position.x() + size.x() && bounds.position.y() < position.y() + size.y())
                && (bounds.position.x() + bounds.size().x() > position.x() && bounds.position.y() + bounds.size().y() > position.y());
    }

    public Bounds move(Vector2f point) {
        return new Bounds(position.add(point), size);
    }

}
