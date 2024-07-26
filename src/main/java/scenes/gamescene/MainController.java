package scenes.gamescene;

import config.ScreenConfig;
import core.Controller;
import core.EngineContext;
import core.Parameters;
import core.ecs.Ecs;
import core.ecs.EcsSnapshot;
import core.ecs.Entity;
import core.ecs.components.Camera;
import core.ecs.components.Position;
import core.graphics.WindowProvider;
import core.input.InputBuffer;
import core.loading.AssetManager;
import core.networking.IoServer;
import core.util.Vector2f;
import core.util.Vector3f;
import scenes.lib.AssetConstants;
import scenes.lib.PlayerController;
import scenes.lib.components.Sprite;
import scenes.lib.components.TerrainChunk;
import scenes.lib.components.UnitInfo;
import scenes.lib.entities.EntityHelper;
import scenes.lib.gui.MainGui;
import scenes.lib.rendering.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

public class MainController extends Controller {

    public static final String PLAYER_ID = "player_id";
    public static final String ECS_SNAPSHOT = "ecs_snapshot";
    public static final String HOSTING_SOCKET = "hosting_socket";
    public static final String CLIENT_SOCKET = "client_socket";

    private NewRenderCanvas canvas;
    private PlayerController playerController;
    private Ecs ecs;
    private InputBuffer inputBuffer;

    private Entity camera;
    private Entity testUnit;

    @Override
    public void init(EngineContext context, Parameters parameters) {
        playerController = new PlayerController(context);
        ecs = context.getService(Ecs.class);
        inputBuffer = context.getService(InputBuffer.class);

        var playerId = parameters.getInt(PLAYER_ID);
        var snapshot = parameters.<EcsSnapshot>get(ECS_SNAPSHOT);
        ecs.loadSnapshot(snapshot);

        // TODO learn what map to load.
        var terrain = ecs.newEntity();
        terrain.setComponent(TerrainChunk.generate(Vector2f.of(25, 25)));

        camera = ecs.newEntity();
        camera.setComponent(new Camera(ScreenConfig.ViewportSize, true));

        centerCameraOnBase(playerId);

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

        var unitMovement = Vector3f.ZERO;

        if(inputBuffer.isKeyDown(KeyEvent.VK_UP)) {
            unitMovement = unitMovement.add(0, 0, -0.1f);
        }
        if(inputBuffer.isKeyDown(KeyEvent.VK_DOWN)) {
            unitMovement = unitMovement.add(0, 0, 0.1f);
        }
        if(inputBuffer.isKeyDown(KeyEvent.VK_LEFT)) {
            unitMovement = unitMovement.add(-0.1f, 0, 0);
        }
        if(inputBuffer.isKeyDown(KeyEvent.VK_RIGHT)) {
            unitMovement = unitMovement.add(0.1f, 0, 0);
        }

        /*
        var unitPos = testUnit.getComponent(Position.class);
        testUnit.setComponent(unitPos.move(unitMovement));
        */

        var position = camera.getComponent(Position.class);
        camera.setComponent(position.move(movement));

        canvas.render();

    }

    @Override
    public void cleanup() {

    }

    private void centerCameraOnBase(int playerId) {
        var units = ecs.view(UnitInfo.class, Position.class);
        var mainBase = units.stream().filter(unit -> {
            var unitInfo = unit.component1();
            return unitInfo.playerId() == playerId && unitInfo.type().equals(UnitInfo.BASE);
        }).findFirst();
        if(mainBase.isPresent()) {
            var mainBaseOrigin = mainBase.get().component2().position().add(1f, 0, 1f);
            var cameraOffset = IsometricHelper.toScreenSpace(mainBaseOrigin).mul(-1);
            camera.setComponent(new Position(cameraOffset.toVector3f()));
        }
    }

    private void setupCanvas(EngineContext context) {
        var assetManager = context.<AssetManager>getService(AssetManager.class);
        var cursor = assetManager.<Cursor>getAsset(AssetConstants.CURSOR);
        var tileSet = assetManager.<TextureAtlas>getAsset(AssetConstants.TEXTURE_ATLAS);

        var windowProvider = context.<WindowProvider>getService(WindowProvider.class);
        var terrainRenderer = new IsometricTerrainRenderer(context, tileSet,true);
        var spriteRenderer = new SpriteRenderer(context, tileSet);
        var fogOfWarRenderer = new FogOfWarRenderer(context, ScreenConfig.ViewportSize);
        canvas = new NewRenderCanvas(java.util.List.of(
                new BufferedRenderer(context, ScreenConfig.ViewportSize, List.of(
                        g2d -> {
                            g2d.setColor(Color.WHITE);
                            g2d.fillRect(0,0, (int) ScreenConfig.ViewportSize.x(), (int) ScreenConfig.ViewportSize.y());
                        },
                        terrainRenderer,
                        spriteRenderer,
                        fogOfWarRenderer
                ))
        ));

        var container = new MainGui(canvas);
        container.setCursor(cursor);
        windowProvider.addComponent(container);

        canvas.init();
    }

}
