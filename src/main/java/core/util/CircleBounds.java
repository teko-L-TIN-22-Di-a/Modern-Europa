package core.util;

public record CircleBounds(Vector2f position, float radius) {

    public boolean intersects(Vector2f point) {
        return Math.sqrt(Math.exp(point.x() - position.x())) + Math.exp((point.y() - position.y())) <= radius;
    }

    public boolean intersects(CircleBounds circle) {
        return Math.sqrt(Math.exp(circle.position.x() - position.x()) + Math.exp((circle.position().x()) - position.y())) <= radius + circle.radius;
    }

    public CircleBounds move(Vector2f point) {
        return new CircleBounds(position.add(point), radius);
    }

}
