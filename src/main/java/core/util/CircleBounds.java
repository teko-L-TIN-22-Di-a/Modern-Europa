package core.util;

public record CircleBounds(Vector2f position, float radius) {

    public boolean intersects(Vector2f point) {
        return Math.sqrt(Math.pow(point.x() - position.x(), 2)) + Math.pow((point.y() - position.y()), 2) <= radius;
    }

    public boolean intersects(CircleBounds circle) {
        return Math.sqrt(Math.pow(circle.position.x() - position.x(), 2) + Math.pow((circle.position().x()) - position.y(), 2)) <= radius + circle.radius;
    }

    public CircleBounds move(Vector2f point) {
        return new CircleBounds(position.add(point), radius);
    }

}
