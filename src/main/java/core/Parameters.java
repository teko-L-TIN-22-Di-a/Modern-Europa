package core;

import java.util.HashMap;
import java.util.Map;

public class Parameters {

    public static Parameters EMPTY = new Parameters(new HashMap<>());

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

    @Override
    public String toString() {
        // Only for debugging maybe clean up in the future?

        if(parameters.isEmpty()) {
            return "{}";
        }

        var string = "";

        for (var entry : parameters.entrySet()) {
            string += "\n" + entry.getKey() + ": \"" + entry.getValue() + "\"";
        }

        string = string.substring(0, string.length() - 1);

        return string;
    }
}
