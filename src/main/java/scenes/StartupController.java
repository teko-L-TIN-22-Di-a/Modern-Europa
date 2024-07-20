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
import java.util.Arrays;
import java.util.HashMap;
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
        var groundTexture = assetManager.<BufferedImage>getAsset("ground.png");
        ImageHelper.keyOut(groundTexture, Color.WHITE);

        var tileSet = new TextureAtlas();
        tileSet.add(Map.ofEntries(
                entry("1", new TextureAtlasEntry(groundTexture, Vector2f.of(0,0), TileConfig.TileSize))
        ));
        tileSet.add(ProcessPlayerAssets());
        assetManager.registerAsset(AssetConstants.TEXTURE_ATLAS, tileSet);
    }

    private Map<String, TextureAtlasEntry> ProcessPlayerAssets() {

        var buildingsTexture = assetManager.<BufferedImage>getAsset("building.png");
        ImageHelper.keyOut(buildingsTexture, Color.WHITE);
        var infantryTextures = assetManager.<BufferedImage>getAsset("infantry.png");
        ImageHelper.keyOut(infantryTextures, Color.WHITE);

        // Base colors
        var entries = new HashMap<>(Map.ofEntries(
                entry(TextureConstants.UNIT, new TextureAtlasEntry(infantryTextures, Vector2f.of(0, 0), Vector2f.of(26, 28))),
                entry(TextureConstants.BASE, new TextureAtlasEntry(buildingsTexture, Vector2f.of(0, 0), Vector2f.of(132, 82))),
                entry(TextureConstants.GENERATOR, new TextureAtlasEntry(buildingsTexture, Vector2f.of(132, 0), Vector2f.of(132, 82)))
        ));

        var colors = Arrays.asList(Color.cyan, Color.red, Color.green, Color.blue);

        // Clean this up.
        var i = 0;
        for(var color : colors) {
            var coloredBuildingsTexture = ImageHelper.clone(buildingsTexture);
            ImageHelper.keyOut(coloredBuildingsTexture, Color.MAGENTA, color.getRGB());
            var coloredInfantryTextures = ImageHelper.clone(infantryTextures);
            ImageHelper.keyOut(coloredInfantryTextures, Color.MAGENTA, color.getRGB());

            entries.putAll(Map.ofEntries(
                    entry(TextureConstants.UNIT + i, new TextureAtlasEntry(coloredInfantryTextures, Vector2f.of(0,0), Vector2f.of(26, 28))),
                    entry(TextureConstants.BASE + i, new TextureAtlasEntry(coloredBuildingsTexture, Vector2f.of(0,0), Vector2f.of(132, 82))),
                    entry(TextureConstants.GENERATOR + i, new TextureAtlasEntry(coloredBuildingsTexture, Vector2f.of(132,0), Vector2f.of(132, 82)))
            ));
            i++;
        }

        return entries;
    }

}
