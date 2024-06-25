package scenes.gamescene;

import config.ScreenConfig;
import core.Controller;
import core.EngineContext;
import core.Parameters;
import core.ecs.Ecs;
import core.graphics.WindowProvider;
import core.loading.AssetManager;
import core.util.Vector2f;
import scenes.lib.AssetConstants;
import scenes.lib.PlayerController;
import scenes.lib.components.TerrainChunk;
import scenes.lib.gui.MainGui;
import scenes.lib.rendering.*;

import java.awt.*;
import java.util.List;

public class MainController extends Controller {

    private NewRenderCanvas canvas;
    private PlayerController playerController;
    private Ecs ecs;

    @Override
    public void init(EngineContext context, Parameters parameters) {
        playerController = new PlayerController(context);
        ecs = context.getService(Ecs.class);

        var terrain = ecs.newEntity();
        terrain.setComponent(new TerrainChunk(Vector2f.of(5, 5)));

        setupCanvas(context);
    }

    @Override
    public void update() {

        canvas.render();

    }

    @Override
    public void cleanup() {

    }

    private void setupCanvas(EngineContext context) {
        var assetManager = context.<AssetManager>getService(AssetManager.class);
        var cursor = assetManager.<Cursor>getAsset(AssetConstants.CURSOR);
        var tileSet = assetManager.<TileSet>getAsset(AssetConstants.TILE_SET);

        var windowProvider = context.<WindowProvider>getService(WindowProvider.class);
        var terrainRenderer = new IsometricTerrainRenderer(context, tileSet,true);
        canvas = new NewRenderCanvas(java.util.List.of(
                new BufferedRenderer(context, ScreenConfig.ViewportSize, List.of(
                        g2d -> {
                            g2d.setColor(Color.WHITE);
                            g2d.fillRect(0,0, (int) ScreenConfig.ViewportSize.x(), (int) ScreenConfig.ViewportSize.y());
                        },
                        terrainRenderer
                ))
        ));

        var container = new MainGui(canvas);
        container.setCursor(cursor);
        windowProvider.addComponent(container);

        canvas.init();
    }

}
