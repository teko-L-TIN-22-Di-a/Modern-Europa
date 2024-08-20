package scenes.startupscene;

import core.Controller;
import core.ControllerSwitcher;
import core.EngineContext;
import core.Parameters;
import core.graphics.ImageHelper;
import core.loading.AssetLoader;
import core.loading.AssetManager;
import core.loading.LoadConfiguration;
import scenes.lib.AssetConstants;

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
    public void update(double delta) {
        switcher.switchTo(nextController);
    }

    @Override
    public void cleanup() {
        // Ignore
    }

    private void Load() {
        assetLoader.load(Map.ofEntries(
                entry("attack.png", LoadConfiguration.DefaultImage),
                entry("tile-sheet.png", LoadConfiguration.DefaultImage),
                entry("cursor.png", LoadConfiguration.DefaultImage),
                entry("combat-units.png", LoadConfiguration.DefaultImage),
                entry("buildings.png", LoadConfiguration.DefaultImage)
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
        var tileSheet = assetManager.<BufferedImage>getAsset("tile-sheet.png");
        var attackSheet = assetManager.<BufferedImage>getAsset("attack.png");
        var buildingsSheet = assetManager.<BufferedImage>getAsset("buildings.png");
        var combatUnitSheet = assetManager.<BufferedImage>getAsset("combat-units.png");

        var assetLoader = new scenes.startupscene.AssetLoader()
                .loadTileSheet(tileSheet)
                .loadAttackSheet(attackSheet)
                .loadBuildingSheet(buildingsSheet)
                .loadCombatUnitSheet(combatUnitSheet);

        assetManager.registerAsset(AssetConstants.TEXTURE_ATLAS, assetLoader.getTextureAtlas());
        assetManager.registerAsset(AssetConstants.ANIMATION_ATLAS, assetLoader.getAnimationAtlas());
    }

}
