package core.ecs;

import core.EngineContext;
import core.EngineEventHooks;

import java.util.*;

public class Ecs {

    private int currentId = 0;

    private final List<Entity> entities = new LinkedList<Entity>();
    private final Map<String, Map<Integer, Object>> componentMaps = new HashMap<>();

    public void clear() {
        currentId = 0;
        entities.clear();
        componentMaps.clear();
    }

    public EcsSnapshot getSnapshot() {
        return new EcsSnapshot(
                currentId,
                new ArrayList<>(entities.stream().map(entity -> new SnapshotEntity(entity.id())).toList()),
                new HashMap<>(componentMaps));
    }

    /**
     * Completely overrides to the state the snapshot was in.
     * @param snapshot
     */
    public void loadSnapshot(EcsSnapshot snapshot) {
        clear();
        currentId = snapshot.currentId();
        entities.addAll(snapshot.entities().stream().map(entity -> new Entity(this, entity.id())).toList());
        componentMaps.putAll(snapshot.componentMaps());
    }

    public Entity newEntity() {
        var entity = new Entity(this, currentId++);
        entities.add(entity);
        return entity;
    }
    public Entity getEntity(int id) {
        for(var e : entities) {
           if(e.id() == id) {
               return e;
           }
        }
        return null;
    }

    public <T, T2> List<EcsView2<T,T2>> view(Class<T> type, Class<T2> type2) {
        var componentMap = tryGetComponentMap(type);
        var componentMap2 = tryGetComponentMap(type2);

        var filteredList = new LinkedList<EcsView2<T, T2>>();

        for(var entry : componentMap.entrySet()) {

            if(!componentMap2.containsKey(entry.getKey())) {
                continue;
            }

            //noinspection unchecked
            filteredList.push(new EcsView2<>(
                    entry.getKey(),
                    (T) entry.getValue(),
                    (T2) componentMap2.get(entry.getKey())));
        }

        return filteredList;
    }

    public <T> List<EcsView<T>> view(Class<T> type) {
        var componentMap = tryGetComponentMap(type);

        var filteredList = new LinkedList<EcsView<T>>();

        for(var entry : componentMap.entrySet()) {
            //noinspection unchecked
            filteredList.push(new EcsView<>(entry.getKey(), (T) entry.getValue()));
        }

        return filteredList;
    }

    public <T> T getComponent(int entityId, Class<?> type) {
        var componentMap = tryGetComponentMap(type);

        //noinspection unchecked
        return (T) componentMap.get(entityId);
    }

    public void setComponent(int entityId, Record component, Class<?> type) {
        var componentMap = tryGetComponentMap(type);

        componentMap.put(entityId, component);
    }

    public void delete(int entityId) {
        entities.removeIf(e -> e.id() == entityId);
        for(var component : componentMaps.values()) {
            component.remove(entityId);
        }
    }

    public void setComponent(int entityId, Record component) {
        setComponent(entityId, component, component.getClass());
    }

    private Map<Integer, Object> tryGetComponentMap(Class<?> type) {
        var typeKey = type.getName();
        if(componentMaps.containsKey(typeKey)) {
            return componentMaps.get(typeKey);
        }

        var newMap = new HashMap<Integer, Object>();
        componentMaps.put(typeKey, newMap);
        return newMap;
    }

    public void cleanup() {
        this.clear();
    }

    public static EngineContext.Builder addToServices(EngineContext.Builder builder) {
        builder.addService(Ecs.class, new Ecs());

        return builder;
    }

    public static EngineContext init(EngineContext context) {

        var ecs = context.<Ecs>getService(Ecs.class);

        if (ecs == null) {
            throw new RuntimeException("No WindowProvider found to initialise. Call addToServices first.");
        }

        var engineHooks = context.<EngineEventHooks>getService(EngineEventHooks.class);
        engineHooks.bindInitController(x -> ecs.cleanup());

        return context;
    }

}
