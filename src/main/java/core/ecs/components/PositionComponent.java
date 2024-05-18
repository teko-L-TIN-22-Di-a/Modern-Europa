package core.ecs.components;

import core.util.Vector2f;

public record PositionComponent(Vector2f position) {
    public PositionComponent move(Vector2f position) {
        return new PositionComponent(this.position.add(position));
    }
}
