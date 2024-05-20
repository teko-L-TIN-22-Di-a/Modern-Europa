package core.util;

public record Bounds(Vector2f position, Vector2f size) {

    public boolean inersects(Vector2f point) {
        return (point.x() < position.x() + size.x() && point.y() < position.y() + size.y())
                && (point.x() > position.x() && point.y() > position.y());
    }

    public Bounds move(Vector2f point) {
        return new Bounds(position.add(point), size);
    }

}
