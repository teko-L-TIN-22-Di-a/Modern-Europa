package scenes;

import config.TileConfig;
import core.Controller;
import core.ControllerSwitcher;
import core.EngineContext;
import core.Parameters;
import core.graphics.ImageHelper;
import core.loading.AssetLoader;
import core.loading.AssetManager;
import core.loading.LoadConfiguration;
import core.util.Vector2f;
import scenes.lib.AssetConstants;
import scenes.lib.TextureConstants;
import scenes.lib.rendering.TextureAtlas;
import scenes.lib.rendering.TextureAtlasEntry;

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
    public void init(EngineContext context, Parameters parameters) {
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
                entry("cursor.png", LoadConfiguration.DefaultImage),
                entry("infantry.png", LoadConfiguration.DefaultImage),
                entry("building.png", LoadConfiguration.DefaultImage)
        ));
    }

    private void ProcessAssets() {
        // Cursor
        var cursorImage = assetManager.<BufferedImage>getAsset("cursor.png");
        ImageHelper.keyOut(cursorImage, Color.WHITE);

        var toolkit = Toolkit.getDefaultToolkit();
        var cursor = toolkit.createCustomCursor(cursorImage, new Point(0, 0), "cursor");
        assetManager.registerAsset(AssetConstants.CURSOR, cursor);

        // Texture atlas
        var testImage = assetManager.<BufferedImage>getAsset("ground.png");
        ImageHelper.keyOut(testImage, Color.WHITE);
        var testBase = assetManager.<BufferedImage>getAsset("building.png");
        ImageHelper.keyOut(testBase, Color.WHITE);
        var testInfantry = assetManager.<BufferedImage>getAsset("infantry.png");
        ImageHelper.keyOut(testInfantry, Color.WHITE);

        var tileSet = new TextureAtlas();
        tileSet.add(Map.ofEntries(
                entry("1", new TextureAtlasEntry(testImage, Vector2f.of(0,0), TileConfig.TileSize)),
                entry(TextureConstants.UNIT, new TextureAtlasEntry(testInfantry, Vector2f.of(0,0), Vector2f.of(26, 28))),
                entry(TextureConstants.BASE, new TextureAtlasEntry(testBase, Vector2f.of(0,0), Vector2f.of(132, 82))),
                entry(TextureConstants.GENERATOR, new TextureAtlasEntry(testBase, Vector2f.of(132,0), Vector2f.of(132, 82)))
        ));
        assetManager.registerAsset(AssetConstants.TEXTURE_ATLAS, tileSet);
    }

}
