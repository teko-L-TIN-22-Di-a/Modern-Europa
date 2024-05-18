package core.ecs;

import core.ecs.components.PositionComponent;
import core.util.Vector2f;

public record Entity(Ecs ecs, int id) {

    public Entity(Ecs ecs, int id) {
        this.ecs = ecs;
        this.id = id;

        setComponent(new PositionComponent(Vector2f.ZERO));
    }

    public <T> T getComponent(Class<T> componentClass) {
        return ecs.getComponent(id, componentClass);
    }

    public Entity setComponent(Object component, Class<?> componentClass) {
        ecs.setComponent(id, component, componentClass);
        return this;
    }

    public Entity setComponent(Object component) {
        return setComponent(component, component.getClass());
    }

}
