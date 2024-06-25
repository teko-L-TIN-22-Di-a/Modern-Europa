package scenes.lib.networking;

import com.google.gson.Gson;
import core.util.JsonConverter;

public record SocketMessage(String type, Object value) {
    public final static Gson gson = JsonConverter.getInstance();

    public <T> T getMessage(Class<T> type) {
        // TODO Check for a better solution Maybe a bit hacky
        var json = gson.toJson(value);
        return gson.fromJson(json, type);
    }
    public static SocketMessage of(Object value) {
        return new SocketMessage(value.getClass().getSimpleName(), value);
    }
}
