package core.loading;

import core.EngineContext;

import java.util.HashMap;
import java.util.Map;

public class DefaultAssetManager implements AssetManager {

    private final Map<String, Object> registeredAssets = new HashMap<>();

    @Override
    public void registerAsset(String path, Object asset) {
        registeredAssets.put(path, asset);
    }

    @Override
    public <T> T getAsset(String path) {
        if(!registeredAssets.containsKey(path)) {
            return null;
        }

        //noinspection unchecked
        return (T) registeredAssets.get(path);
    }

    public static EngineContext.Builder addToServices(EngineContext.Builder builder) {
        builder.addService(AssetManager.class, new DefaultAssetManager());
        return builder;
    }
}
