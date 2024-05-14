package core.loading;

public interface AssetManager {

    void registerAsset(String path, Object asset);
    <T> T getAsset(String path);

}
