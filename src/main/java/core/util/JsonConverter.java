package core.util;

import com.google.gson.Gson;

public class JsonConverter {

    private final static Gson gson = new Gson();

    public static Gson getInstance() {
        return gson;
    }

}
