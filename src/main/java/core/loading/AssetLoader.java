package core.loading;

import rx.subjects.ReplaySubject;

import java.util.Map;

public interface AssetLoader {

    ReplaySubject<Void> loadAsync(Map<String, LoadConfiguration> loadConfigurations);

    void load(Map<String, LoadConfiguration> loadConfigurations);

    void load(String path, LoadConfiguration configuration);

}
