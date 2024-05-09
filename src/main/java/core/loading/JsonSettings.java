package core.loading;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import core.Engine;
import core.EngineContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

public class JsonSettings implements Settings {
    protected static final Logger logger = LogManager.getLogger(JsonSettings.class);

    private final Gson gson = new Gson();
    private Map<String, Object> settingsMap = new HashMap<>();
    private String filePath;

    private JsonSettings(String file) {
        filePath = getClass().getClassLoader().getResource(file).getFile().replaceFirst("/", "");
        //filePath = Paths.get(System.getProperty("user.dir"), file).toString();
    }

    public void load() {
        if(!Files.exists(Path.of(filePath))) {
            return;
        }

        try {
            var json = Files.readString(Path.of(filePath));

            var typeToken = new TypeToken<Map<String, Object>>() {};
            settingsMap = gson.fromJson(json, typeToken.getType());
        } catch (IOException e) {
            logger.error("Could not read jsonSettings file {}. Exception: {}", filePath, e.getMessage());
        }
    }

    public void put(String path, Object value) {
        settingsMap.put(path, gson.toJson(value));
    }

    public void save() {
        try {
            Files.writeString(
                    Path.of(filePath),
                    gson.toJson(settingsMap),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            logger.error("Could not save jsonSettings file {}. Exception: {}", filePath, e.getMessage());
        }
    }

    public <T> T get(Class<T> type) {
        var path = type.getSimpleName();
        if(!settingsMap.containsKey(path)) {
            return null;
        }

        var json = gson.toJson(settingsMap.get(path));
        return gson.fromJson(json, type);
    }

    public static EngineContext.Builder addToServices(EngineContext.Builder builder, String path) {
        var instance = new JsonSettings(path);
        builder.addService(Settings.class, instance);
        instance.load();

        return builder;
    }

}


