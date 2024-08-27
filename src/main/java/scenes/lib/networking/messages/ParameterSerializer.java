package scenes.lib.networking.messages;


import com.google.gson.*;
import core.ecs.components.Camera;
import core.ecs.components.Position;
import core.util.Vector2f;
import core.util.Vector3f;
import scenes.gamescene.commands.CommandConstants;
import scenes.lib.components.*;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ParameterSerializer implements JsonSerializer<Map<String, Object>>, JsonDeserializer<Map<String, Object>> {

    @Override
    public JsonElement serialize(Map<String, Object> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        for (Map.Entry<String, Object> entry : src.entrySet()) {
            jsonObject.add(entry.getKey(), context.serialize(entry.getValue()));
        }

        return jsonObject;
    }

    @Override
    public Map<String, Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Map<String, Object> map = new HashMap<>();

        JsonObject jsonObject = json.getAsJsonObject();

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            map.put(entry.getKey(), context.deserialize(entry.getValue(), Object.class));

            // TODO Very hacky find a better solution
            var parameterMap = Map.ofEntries(
                    Map.entry(CommandConstants.AUTO_ATTACK_ORIGIN, Vector3f.class),
                    Map.entry(CommandConstants.MOVEMENT_TARGET_POSITION, Vector2f.class),
                    Map.entry(CommandConstants.BUILDING_CREATION_POSITION, Vector2f.class),
                    Map.entry(CommandConstants.BUILDING_CREATION_PLAYER_ID, Integer.class),
                    Map.entry(CommandConstants.MINING_PLAYER_ID, Integer.class)
            );

            var mappedType = parameterMap.getOrDefault(entry.getKey(), null);

            if(mappedType != null) {
                map.put(entry.getKey(), context.deserialize(entry.getValue(), mappedType));
            } else {
                map.put(entry.getKey(), context.deserialize(entry.getValue(), Object.class));
            }

        }

        return map;
    }
}
