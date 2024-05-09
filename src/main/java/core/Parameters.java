package core;

import java.util.HashMap;
import java.util.Map;

public class Parameters {

    public static Parameters Empty = new Parameters(new HashMap<>());

    private final Map<String, Object> parameters;

    public Parameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public <T> T get(String key) {
        //noinspection unchecked
        return (T) parameters.getOrDefault(key, null);
    }

    public String getString(String key) {
        return get(key);
    }

    public int getInt(String key) {
        return get(key);
    }

    public double getDouble(String key) {
        return get(key);
    }

}
