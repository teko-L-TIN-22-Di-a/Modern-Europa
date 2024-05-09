package core.loading;

import core.EngineContext;

public class DefaultAssetManager implements AssetManager {

    public static EngineContext.Builder addToServices(EngineContext.Builder builder) {
        builder.addService(AssetManager.class, new DefaultAssetManager());
        return builder;
    }

}
