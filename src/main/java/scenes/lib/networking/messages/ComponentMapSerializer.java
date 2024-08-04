package scenes.lib.networking.messages;


import com.google.gson.*;
import core.ecs.components.Camera;
import core.ecs.components.Position;
import scenes.lib.components.*;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ComponentMapSerializer implements JsonSerializer<Map<String, Map<Integer, Object>>>, JsonDeserializer<Map<String, Map<Integer, Object>>> {

    @Override
    public JsonElement serialize(Map<String, Map<Integer, Object>> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<String, Map<Integer, Object>> entry : src.entrySet()) {
            JsonObject innerMap = new JsonObject();
            for (Map.Entry<Integer, Object> innerEntry : entry.getValue().entrySet()) {
                innerMap.add(innerEntry.getKey().toString(), context.serialize(innerEntry.getValue()));
            }
            jsonObject.add(entry.getKey(), innerMap);
        }
        return jsonObject;
    }

    @Override
    public Map<String, Map<Integer, Object>> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Map<String, Map<Integer, Object>> map = new HashMap<>();
        JsonObject jsonObject = json.getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            Map<Integer, Object> innerMap = new HashMap<>();
            JsonObject innerJsonObject = entry.getValue().getAsJsonObject();
            for (Map.Entry<String, JsonElement> innerEntry : innerJsonObject.entrySet()) {

                // TODO Very hacky find a better solution
                var types = Arrays.asList(
                        Position.class,
                        Camera.class,
                        Sprite.class,
                        TerrainChunk.class,
                        Tile.class,
                        UnitInfo.class,
                        Selection.class,
                        PathFindingTarget.class,
                        Command.class,
                        NetSynch.class
                );

                var matchingType = types.stream().filter(x -> x.getName().equals(entry.getKey())).findFirst();

                if(matchingType.isPresent()) {
                    innerMap.put(Integer.parseInt(innerEntry.getKey()), context.deserialize(innerEntry.getValue(), matchingType.get()));
                } else {
                    innerMap.put(Integer.parseInt(innerEntry.getKey()), context.deserialize(innerEntry.getValue(), Object.class));
                }
            }
            map.put(entry.getKey(), innerMap);
        }
        return map;
    }
}
