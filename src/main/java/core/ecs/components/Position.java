package core.ecs.components;

import core.util.Vector2f;

public record Position(Vector2f position) {
    public Position move(Vector2f position) {
        return new Position(this.position.add(position));
    }
}
