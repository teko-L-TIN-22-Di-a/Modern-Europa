package core.loading;

import core.EngineContext;
import core.graphics.ImageHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rx.subjects.ReplaySubject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.nio.file.Paths;
import java.util.Map;

public class FileAssetLoader implements AssetLoader {
    protected static final Logger logger = LogManager.getLogger(FileAssetLoader.class);

    private String basePath = "";
    private AssetManager assetManager;

    private FileAssetLoader() {}

    private void init(AssetManager manager) {
        assetManager = manager;
        basePath = PathHelper.getResourcePath();
    }

    public ReplaySubject<Void> loadAsync(Map<String, LoadConfiguration> loadConfigurations) {
        var finishedLoading = ReplaySubject.<Void>create();

        var loadThread = new Thread() {
            public void run() {
                load(loadConfigurations);
                finishedLoading.onNext(null);
                finishedLoading.onCompleted();
            }
        };

        loadThread.start();

        return finishedLoading;
    }

    public void load(Map<String, LoadConfiguration> loadConfigurations) {
        loadConfigurations.forEach(this::load);
    }

    public void load(String path, LoadConfiguration configuration) {
        try {
            var fullPath = Paths.get(basePath, path).toString();

            switch(configuration.type()) {
                case Font -> {
                    var font = Font.createFont(Font.TRUETYPE_FONT, new File(fullPath));
                    assetManager.registerAsset(path, font);
                    logger.debug("Loaded and registered font: {}", path);
                }
                case Image -> {
                    var image = ImageIO.read(new File(fullPath));
                    assetManager.registerAsset(path, ImageHelper.improveFormat(image));
                    logger.debug("Loaded and registered image: {}", path);
                }
                default -> {
                    logger.error("Unkown asset type: {}", configuration.type());
                }
            }
        }catch(Exception e) {
            logger.error(
                    "Could not load asset: {} of type: {} exception: {}",
                    path,
                    configuration.type(),
                    e.getMessage());
        }
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
