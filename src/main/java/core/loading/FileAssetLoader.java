package core.loading;

import core.EngineContext;
import rx.subjects.AsyncSubject;
import rx.subjects.Subject;

import java.util.Map;

public class FileAssetLoader implements AssetLoader {

    private AssetManager assetManager;

    private FileAssetLoader() {}

    public void init(AssetManager manager) {
        assetManager = manager;
    }

    public Subject loadAsync(Map<String, LoadConfiguration> loadConfigurations) {
        var finishedLoading = AsyncSubject.create();

        // TODO improve / remove race condition.
        var loadThread = new Thread() {
            public void run() {
                load(loadConfigurations);
                finishedLoading.onCompleted();
            }
        };

        loadThread.start();

        return finishedLoading;
    }

    public void load(Map<String, LoadConfiguration> loadConfigurations) {

    }

    public static EngineContext.Builder addToServices(EngineContext.Builder builder) {
        builder.addService(AssetLoader.class, new FileAssetLoader());
        return builder;
    }

    public static EngineContext init(EngineContext context) {

        var assetLoader = context.<AssetLoader>getService(AssetLoader.class);
        var manager = context.<AssetManager>getService(AssetManager.class);

        if(assetLoader == null) {
            throw new RuntimeException("No asset loader found, make sure to call FileAssetLoader.addToServices beforehand.");
        }

        if(!(assetLoader instanceof FileAssetLoader instance)) {
            throw new RuntimeException("A different assetLoader was registered.");
        }

        if(manager == null) {
            throw new RuntimeException("No asset manager found, pleaser register an asset manager.");
        }

        instance.init(manager);

        return context;
    }

}
