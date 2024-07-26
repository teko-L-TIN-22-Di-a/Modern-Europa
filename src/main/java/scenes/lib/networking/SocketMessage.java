package scenes.lib.networking;

import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import core.util.JsonConverter;

import java.util.Map;

public record SocketMessage(String type, Object value) {
    public final static Gson gson = new GsonBuilder()
            .registerTypeAdapter(new TypeToken<Map<String, Map<Integer, Object>>>() {}.getType(), new ComponentMapSerializer())
            .create();

    public <T> T getMessage(Class<T> type) {
        // TODO Check for a better solution Maybe a bit hacky
        var json = gson.toJson(value);
        return gson.fromJson(json, type);
    }
    public static SocketMessage of(Object value) {
        return new SocketMessage(value.getClass().getSimpleName(), value);
    }
}
