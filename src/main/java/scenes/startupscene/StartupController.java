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
import scenes.lib.TextureConstants;
import scenes.lib.rendering.TextureAtlas;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Map;

import static scenes.lib.config.RenderingConfig.*;
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
        var buildingsSheet = assetManager.<BufferedImage>getAsset("buildings.png");
        var combatUnitSheet = assetManager.<BufferedImage>getAsset("combat-units.png");

        ImageHelper.keyOut(tileSheet, Color.WHITE);
        ImageHelper.keyOut(buildingsSheet, Color.WHITE);
        ImageHelper.keyOut(combatUnitSheet, Color.WHITE);

        var tileSet = new TextureAtlas();
        tileSet.addSplit(TextureConstants.TILE_SHEET, tileSheet, TILE_SIZE);
        tileSet.addSingleFrame(TextureConstants.DEFAULT_GROUND, TextureConstants.TILE_SHEET, 0);
        tileSet.addSingleFrame(TextureConstants.MINEABLE_GROUND, TextureConstants.TILE_SHEET, 1);
        tileSet.addSingleFrame(TextureConstants.HIGHLIGHT, TextureConstants.TILE_SHEET, 2);
        tileSet.addSingleFrame(TextureConstants.HIGHLIGHT_ERROR, TextureConstants.TILE_SHEET, 3);

        tileSet.addSplit(TextureConstants.BUILDINGS_SHEET, buildingsSheet, BUILDING_SIZE);
        tileSet.addSingleFrame(TextureConstants.BASE, TextureConstants.BUILDINGS_SHEET, 0);
        tileSet.addSingleFrame(TextureConstants.GENERATOR, TextureConstants.BUILDINGS_SHEET, 1);
        tileSet.addSingleFrame(TextureConstants.MINER, TextureConstants.BUILDINGS_SHEET, 2);
        tileSet.addSingleFrame(TextureConstants.CONSTRUCTION_SITE, TextureConstants.BUILDINGS_SHEET, 3);

        tileSet.addSplit(TextureConstants.COMBAT_UNIT_SHEET, combatUnitSheet, COMBAT_UNIT_SIZE);
        tileSet.addSingleFrame(TextureConstants.UNIT, TextureConstants.COMBAT_UNIT_SHEET, 0);
        tileSet.addSingleFrame(TextureConstants.SMALL_UNIT, TextureConstants.COMBAT_UNIT_SHEET, 4);

        var colors = Arrays.asList(Color.cyan, Color.red, Color.green, Color.blue);

        var i = 0;
        for(var color : colors) {
            var coloredBuildingsSheet = ImageHelper.clone(buildingsSheet);
            var coloredCombatUnitSheet = ImageHelper.clone(combatUnitSheet);

            ImageHelper.keyOut(coloredBuildingsSheet, Color.MAGENTA, color.getRGB());
            ImageHelper.keyOut(coloredCombatUnitSheet, Color.MAGENTA, color.getRGB());

            tileSet.addSplit(TextureConstants.BUILDINGS_SHEET + i, coloredBuildingsSheet, BUILDING_SIZE);
            tileSet.addSingleFrame(TextureConstants.BASE+i, TextureConstants.BUILDINGS_SHEET+i, 0);
            tileSet.addSingleFrame(TextureConstants.GENERATOR+i, TextureConstants.BUILDINGS_SHEET+i, 1);
            tileSet.addSingleFrame(TextureConstants.MINER+i, TextureConstants.BUILDINGS_SHEET+i, 2);
            tileSet.addSingleFrame(TextureConstants.CONSTRUCTION_SITE+i, TextureConstants.BUILDINGS_SHEET+i, 3);

            tileSet.addSplit(TextureConstants.COMBAT_UNIT_SHEET + i, coloredCombatUnitSheet, COMBAT_UNIT_SIZE);
            tileSet.addSingleFrame(TextureConstants.UNIT+i, TextureConstants.COMBAT_UNIT_SHEET + i, 0);
            tileSet.addSingleFrame(TextureConstants.SMALL_UNIT+i, TextureConstants.COMBAT_UNIT_SHEET + i, 4);

            i++;
        }

        assetManager.registerAsset(AssetConstants.TEXTURE_ATLAS, tileSet);
    }

}
