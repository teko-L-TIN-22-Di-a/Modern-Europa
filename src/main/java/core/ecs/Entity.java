package core.ecs;

import core.ecs.components.Position;
import core.util.Vector2f;
import core.util.Vector3f;

public record Entity(Ecs ecs, int id) {

    public Entity(Ecs ecs, int id) {
        this.ecs = ecs;
        this.id = id;

        setComponent(new Position(Vector3f.ZERO));
    }

    public <T> T getComponent(Class<T> componentClass) {
        return ecs.getComponent(id, componentClass);
    }

    public Entity setComponent(Record component, Class<?> componentClass) {
        ecs.setComponent(id, component, componentClass);
        return this;
    }

    public Entity setComponent(Record component) {
        return setComponent(component, component.getClass());
    }

    public void delete() {
        ecs.delete(id);
    }

}
