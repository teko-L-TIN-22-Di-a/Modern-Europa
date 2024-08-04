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
import core.input.JFrameMouseListener;
import core.input.MouseListener;
import core.loading.AssetManager;
import core.networking.IoClient;
import core.networking.IoServer;
import core.util.Vector2f;
import core.util.Vector3f;
import scenes.gamescene.rendering.SelectionRenderer;
import scenes.lib.AssetConstants;
import scenes.lib.PlayerInfo;
import scenes.lib.components.TerrainChunk;
import scenes.lib.components.UnitInfo;
import scenes.lib.gui.MainGui;
import scenes.lib.rendering.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

public class MainController extends Controller {

    public static final String PLAYER_ID = "player_id";
    public static final String PLAYERS = "players";
    public static final String ECS_SNAPSHOT = "ecs_snapshot";
    public static final String SERVER_SOCKET = "hosting_socket";
    public static final String CLIENT_SOCKET = "client_socket";

    private static final Color CLEAR_COLOR = new Color(50, 50, 50);

    private NewRenderCanvas canvas;
    private PlayerHandler playerHandler;
    private CameraHandler cameraHandler;
    private Ecs ecs;

    private ServerHandler server;
    private ClientHandler client;

    @Override
    public void init(EngineContext context, Parameters parameters) {
        ecs = context.getService(Ecs.class);

        var playerId = parameters.getInt(PLAYER_ID);
        var snapshot = parameters.<EcsSnapshot>get(ECS_SNAPSHOT);
        var server = parameters.<IoServer>get(SERVER_SOCKET);
        var players = parameters.<List<PlayerInfo>>get(PLAYERS);
        var client = parameters.<IoClient>get(CLIENT_SOCKET);

        if(server != null) {
            this.server = initServer(server, players);
        } else if (client != null) {
            this.client = initClient(client);
        }

        ecs.loadSnapshot(snapshot);

        setupTerrain();
        var renderingContext = setupRendering(context, playerId);

        playerHandler = new PlayerHandler(context, renderingContext, playerId);
        setupCamera(context, playerId);
    }

    @Override
    public void update() {
        playerHandler.update();
        cameraHandler.update();

        canvas.render();
    }

    @Override
    public void cleanup() {
        if(server != null) {
            server.dispose();
        }
        if(client != null) {
            client.dispose();
        }
    }

    private ServerHandler initServer(IoServer server, List<PlayerInfo> players) {
        var handler = new ServerHandler(server, players);
        handler.init();
        return handler;
    }

    private ClientHandler initClient(IoClient client) {
        var handler = new ClientHandler(client);
        handler.init();
        return handler;
    }

    private Position getMainBasePosition(int playerId) {
        var units = ecs.view(UnitInfo.class, Position.class);
        var mainBase = units.stream().filter(unit -> {
            var unitInfo = unit.component1();
            return unitInfo.playerId() == playerId && unitInfo.type().equals(UnitInfo.BASE);
        }).findFirst();
        if(mainBase.isPresent()) {
            return mainBase.get().component2();
        }

        return new Position(Vector3f.ZERO);
    }

    private void setupTerrain() {
        // TODO implement proper maps.
        var terrain = ecs.newEntity();
        terrain.setComponent(TerrainChunk.generate(Vector2f.of(25, 25)));
    }

    private void setupCamera(EngineContext context, int playerId) {
        var camera = ecs.newEntity();
        camera.setComponent(new Camera(ScreenConfig.ViewportSize, true));
        cameraHandler = new CameraHandler(context, camera);
        cameraHandler.centerCameraOnMainBase(getMainBasePosition(playerId));
    }

    private RenderingContext setupRendering(EngineContext context, int playerId) {
        var mouseListener = context.<MouseListener>getService(MouseListener.class);
        var assetManager = context.<AssetManager>getService(AssetManager.class);
        var cursor = assetManager.<Cursor>getAsset(AssetConstants.CURSOR);
        var tileSet = assetManager.<TextureAtlas>getAsset(AssetConstants.TEXTURE_ATLAS);

        var windowProvider = context.<WindowProvider>getService(WindowProvider.class);
        var scale = windowProvider.getWindowSize().div(ScreenConfig.ViewportSize.x(), ScreenConfig.ViewportSize.y());

        var terrainRenderer = new IsometricTerrainRenderer(context, tileSet,true);
        var spriteRenderer = new SpriteRenderer(context, tileSet);
        var fogOfWarRenderer = new FogOfWarRenderer(context, ScreenConfig.ViewportSize, playerId);
        var selectionRenderer = new SelectionRenderer(context, scale);

        canvas = new NewRenderCanvas(List.of(
                new BufferedRenderer(context, ScreenConfig.ViewportSize, List.of(
                        g2d -> {
                            g2d.setColor(CLEAR_COLOR);
                            g2d.fillRect(0,0, (int) ScreenConfig.ViewportSize.x(), (int) ScreenConfig.ViewportSize.y());
                        },
                        terrainRenderer,
                        spriteRenderer,
                        fogOfWarRenderer,
                        selectionRenderer
                ))
        ));
        // Fixing canvas having weird mouse listener support.
        if(mouseListener instanceof JFrameMouseListener instance) {
            canvas.addMouseMotionListener(instance);
            canvas.addMouseListener(instance);
            canvas.addMouseWheelListener(instance);
        }

        var container = new MainGui(canvas);
        container.setCursor(cursor);
        windowProvider.addComponent(container);
        canvas.init();

        return new RenderingContext(selectionRenderer, container);
    }

}
