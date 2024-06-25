package core.loading;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.nio.file.Path;

public class PathHelper {
    protected static final Logger logger = LogManager.getLogger(PathHelper.class);

    public static String getResourcePath() {
        var baseUrl = ClassLoader.getSystemClassLoader().getResource("");

        if(baseUrl == null) return "";

        try {
            return Path.of(baseUrl.toURI()).toAbsolutePath().toString();
        } catch (Exception e) {
            logger.error("Failed to load resourcePath: {}", e.getMessage());
        }

        return "";
    }
}
