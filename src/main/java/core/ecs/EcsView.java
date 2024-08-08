package core.ecs;

public record EcsView<T>(int entityId, T component) {
}
