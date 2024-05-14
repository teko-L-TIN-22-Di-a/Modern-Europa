package core.loading;

import java.net.URL;

public class PathHelper {
    public static String getResourcePath() {
        var baseUrl = PathHelper.class.getClassLoader().getResource("");

        if(baseUrl == null) return "";

        return baseUrl
                .getPath()
                .replaceFirst("/", "");
    }
}
