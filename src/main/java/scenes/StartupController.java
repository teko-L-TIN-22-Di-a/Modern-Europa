package scenes;

import core.Controller;
import core.ControllerSwitcher;
import core.EngineContext;
import core.graphics.ImageHelper;
import core.loading.AssetLoader;
import core.loading.AssetManager;
import core.loading.LoadConfiguration;
import core.util.Vector2f;
import scenes.lib.AssetConstants;
import scenes.lib.rendering.TileSet;
import scenes.lib.rendering.TileSetConfiguration;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

import static java.util.Map.entry;

public class StartupController extends Controller {

    private final Controller nextController;

    private ControllerSwitcher switcher;
    private AssetLoader assetLoader;
    private AssetManager assetManager;

    public StartupController(Controller next) {
        nextController = next;
    }

    @Override
    public void init(EngineContext context) {
        switcher = context.getService(ControllerSwitcher.class);
        assetLoader = context.getService(AssetLoader.class);
        assetManager = context.getService(AssetManager.class);

        Load();
        ProcessAssets();
    }

    @Override
    public void update() {
        switcher.switchTo(nextController);
    }

    @Override
    public void cleanup() {
        // Ignore
    }

    private void Load() {
        assetLoader.load(Map.ofEntries(
                entry("ground.png", LoadConfiguration.DefaultImage),
                entry("cursor.png", LoadConfiguration.DefaultImage)
        ));
    }

    private void ProcessAssets() {
        // Cursor
        var cursorImage = assetManager.<BufferedImage>getAsset("cursor.png");
        ImageHelper.keyOut(cursorImage, Color.WHITE);

        var toolkit = Toolkit.getDefaultToolkit();
        var cursor = toolkit.createCustomCursor(cursorImage, new Point(0, 0), "cursor");
        assetManager.registerAsset(AssetConstants.CURSOR, cursor);

        // Tile set
        var testImage = assetManager.<BufferedImage>getAsset("ground.png");
        ImageHelper.keyOut(testImage, Color.WHITE);

        var tileSet = new TileSet();
        tileSet.add(Map.ofEntries(
                entry("1", new TileSetConfiguration(testImage, Vector2f.of(0,0), Vector2f.of(132, 64)))
        ));
        assetManager.registerAsset(AssetConstants.TILE_SET, tileSet);
    }

}
