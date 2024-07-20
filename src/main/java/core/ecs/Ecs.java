package core.ecs;

import core.EngineContext;
import core.EngineEventHooks;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Ecs {

    private int currentId = 0;

    private List<Entity> entites = new LinkedList<Entity>();
    private Map<Class<?>, Map<Integer, Object>> componentMaps = new HashMap<>();

    public void clear() {
        currentId = 0;
        entites.clear();
        componentMaps.clear();
    }

    public Entity newEntity() {
        return new Entity(this, currentId++);
    }
    public Entity getEntity(int id) {
        for(var e : entites) {
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

    public void setComponent(int entityId, Object component, Class<?> type) {
        var componentMap = tryGetComponentMap(type);

        componentMap.put(entityId, component);
    }

    public void delete(int entityId) {
        entites.removeIf(e -> e.id() == entityId);
        for(var component : componentMaps.values()) {
            component.remove(entityId);
        }
    }

    public void setComponent(int entityId, Object component) {
        setComponent(entityId, component, component.getClass());
    }

    private Map<Integer, Object> tryGetComponentMap(Class<?> type) {
        if(componentMaps.containsKey(type)) {
            return componentMaps.get(type);
        }

        var newMap = new HashMap<Integer, Object>();
        componentMaps.put(type, newMap);
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
