package scenes.gamescene;

import config.ScreenConfig;
import core.Controller;
import core.EngineContext;
import core.Parameters;
import core.ecs.Ecs;
import core.ecs.Entity;
import core.ecs.components.Camera;
import core.ecs.components.Position;
import core.graphics.WindowProvider;
import core.input.InputBuffer;
import core.loading.AssetManager;
import core.util.Vector2f;
import scenes.lib.AssetConstants;
import scenes.lib.PlayerController;
import scenes.lib.components.Sprite;
import scenes.lib.components.TerrainChunk;
import scenes.lib.gui.MainGui;
import scenes.lib.rendering.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

public class MainController extends Controller {

    private NewRenderCanvas canvas;
    private PlayerController playerController;
    private Ecs ecs;
    private InputBuffer inputBuffer;

    private Entity camera;

    @Override
    public void init(EngineContext context, Parameters parameters) {
        playerController = new PlayerController(context);
        ecs = context.getService(Ecs.class);
        inputBuffer = context.getService(InputBuffer.class);

        // Setup Entities
        var terrain = ecs.newEntity();
        terrain.setComponent(new TerrainChunk(Vector2f.of(5, 5)));

        var testSprite = ecs.newEntity();
        testSprite.setComponent(new Sprite("0", Vector2f.ZERO, true));

        camera = ecs.newEntity();
        camera.setComponent(new Camera(ScreenConfig.ViewportSize, true));

        setupCanvas(context);
    }

    @Override
    public void update() {

        var movement = Vector2f.ZERO;

        if(inputBuffer.isKeyDown(KeyEvent.VK_W)) {
            movement = movement.add(0, 10);
        }
        if(inputBuffer.isKeyDown(KeyEvent.VK_S)) {
            movement = movement.add(0, -10);
        }
        if(inputBuffer.isKeyDown(KeyEvent.VK_A)) {
            movement = movement.add(10, 0);
        }
        if(inputBuffer.isKeyDown(KeyEvent.VK_D)) {
            movement = movement.add(-10, 0);
        }

        var position = camera.getComponent(Position.class);
        camera.setComponent(position.move(movement));

        canvas.render();

    }

    @Override
    public void cleanup() {

    }

    private void setupCanvas(EngineContext context) {
        var assetManager = context.<AssetManager>getService(AssetManager.class);
        var cursor = assetManager.<Cursor>getAsset(AssetConstants.CURSOR);
        var tileSet = assetManager.<TextureAtlas>getAsset(AssetConstants.TILE_SET);

        var windowProvider = context.<WindowProvider>getService(WindowProvider.class);
        var terrainRenderer = new IsometricTerrainRenderer(context, tileSet,true);
        var spriteRenderer = new SpriteRenderer(context, tileSet);
        canvas = new NewRenderCanvas(java.util.List.of(
                new BufferedRenderer(context, ScreenConfig.ViewportSize, List.of(
                        g2d -> {
                            g2d.setColor(Color.WHITE);
                            g2d.fillRect(0,0, (int) ScreenConfig.ViewportSize.x(), (int) ScreenConfig.ViewportSize.y());
                        },
                        terrainRenderer,
                        spriteRenderer
                ))
        ));

        var container = new MainGui(canvas);
        container.setCursor(cursor);
        windowProvider.addComponent(container);

        canvas.init();
    }

}
