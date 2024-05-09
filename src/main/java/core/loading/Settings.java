package core.loading;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

public interface Settings {

    void load();

    void put(String path, Object value);

    void save();

    <T> T get(Class<T> type);

}
