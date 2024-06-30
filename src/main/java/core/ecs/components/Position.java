package core.ecs.components;

import core.util.Vector2f;
import core.util.Vector3f;

public record Position(Vector3f position) {
    public Position move(Vector3f position) {
        return new Position(this.position.add(position));
    }
    public Position move(Vector2f position) {
        return new Position(this.position.add(position.toVector3f()));
    }
}
