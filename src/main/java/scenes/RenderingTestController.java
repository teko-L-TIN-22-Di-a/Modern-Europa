package scenes;

import config.ScreenConfig;
import config.TileConfig;
import core.Controller;
import core.EngineContext;
import core.Parameters;
import core.ecs.Ecs;
import core.ecs.Entity;
import core.ecs.components.Camera;
import core.ecs.components.Position;
import core.ecs.helper.CameraHelper;
import core.graphics.ImageHelper;
import core.graphics.WindowProvider;
import core.input.InputBuffer;
import core.input.MouseListener;
import core.loading.*;
import core.util.InterpolateHelper;
import core.util.Vector2f;
import scenes.lib.Tile;
import scenes.lib.components.TerrainChunk;
import scenes.lib.rendering.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class RenderingTestController extends Controller {

    private Ecs ecs;

    private WindowProvider windowProvider;
    private InputBuffer inputBuffer;

    private NewRenderCanvas canvas;
    private BufferedRenderer bufferedRenderer;
    private IsometricTerrainRenderer terrainRenderer;

    private Entity camera;
    private Entity terrain;

    private Vector2f hoverPos = Vector2f.ZERO;
    private Vector2f targetPos = Vector2f.ZERO;

    @Override
    public void init(EngineContext context, Parameters parameters) {

        loadAssets(context);

        var assetManager = context.<AssetManager>getService(AssetManager.class);
        var testImage = assetManager.<BufferedImage>getAsset("test.png");
        ImageHelper.keyOut(testImage, Color.WHITE);

        var cursorImage = assetManager.<BufferedImage>getAsset("cursor.png");
        ImageHelper.keyOut(cursorImage, Color.WHITE);

        var toolkit = Toolkit.getDefaultToolkit();
        var cursor = toolkit.createCustomCursor(cursorImage, new Point(0, 0), "cursor");

        ecs = context.getService(Ecs.class);

        terrain = ecs.newEntity();
        terrain.setComponent(new TerrainChunk(Vector2f.of(2, 2)));

        var terrain2 = ecs.newEntity();
        terrain2.setComponent(new TerrainChunk(Vector2f.of(50, 50)));
        terrain2.setComponent(new Position(Vector2f.of(2, 0)));

        camera = ecs.newEntity();
        camera.setComponent(new Camera(ScreenConfig.ViewportSize, true));

        var whiteNoiseTexture = ImageHelper.newImage(ScreenConfig.ViewportSize);
        var mask = ImageHelper.newImage(ScreenConfig.ViewportSize);
        var maskG2d = mask.getGraphics();
        maskG2d.setColor(new Color(0,0,0,0));
        maskG2d.fillRect(0,0, mask.getWidth(), mask.getHeight());
        maskG2d.setColor(Color.BLACK);
        maskG2d.fillOval(0,0, 64,64);

        var tileSet = new TileSet();
        tileSet.add(Map.ofEntries(
                entry("1", new TileSetConfiguration(testImage, Vector2f.of(0,0), Vector2f.of(62, 30))),
                entry("2", new TileSetConfiguration(testImage, Vector2f.of(0,30), Vector2f.of(62, 30))),
                entry("3", new TileSetConfiguration(testImage, Vector2f.of(62,0), Vector2f.of(62, 30))),
                entry("4", new TileSetConfiguration(testImage, Vector2f.of(62,30), Vector2f.of(62, 30)))
        ));

        windowProvider = context.getService(WindowProvider.class);
        terrainRenderer = new IsometricTerrainRenderer(context, tileSet,true);
        bufferedRenderer = new BufferedRenderer(context, ScreenConfig.ViewportSize, List.of(
                g2d -> {

                    g2d.setColor(Color.WHITE);
                    g2d.fillRect(0,0, (int) ScreenConfig.ViewportSize.x(), (int) ScreenConfig.ViewportSize.y());

                    ImageHelper.drawWhiteNoise(whiteNoiseTexture, 50, 150);
                    var noiseG2d = (Graphics2D) whiteNoiseTexture.getGraphics();
                    noiseG2d.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_IN));
                    noiseG2d.drawImage(mask, 0,0, null);

                    g2d.drawImage(whiteNoiseTexture, 0,0, null);
                },
                terrainRenderer,
                g2d -> {
                    var renderPos = IsometricHelper
                            .toScreenSpace(hoverPos)
                            .sub(Vector2f.of(TileConfig.HalfTileSize.x(), TileConfig.TileSize.y()))
                            .add(CameraHelper.GetCameraOffset(camera));

                    g2d.drawImage(
                            testImage,
                            (int) renderPos.x(), (int) renderPos.y(),
                            (int) renderPos.x() + 66, (int) renderPos.y() + 62,
                            126, 0,
                            126 + 66, 62,
                            null);
                }
        ));
        canvas = new NewRenderCanvas(java.util.List.of(
            bufferedRenderer
        ));

        canvas.setCursor(cursor);
        windowProvider.addComponent(canvas);
        canvas.init();

        inputBuffer = context.getService(InputBuffer.class);
        var mouseListener = context.<MouseListener>getService(MouseListener.class);
        mouseListener.bindMouseMoved(mouseEvent -> {
            var pos = Vector2f.of(mouseEvent.getX(), mouseEvent.getY());
            var tilePos = terrainRenderer.getTilePosition(pos.div(bufferedRenderer.getScale()));
            if(tilePos != null) {
                targetPos = tilePos;
            }
        });
        mouseListener.bindMouseClicked(mouseEvent -> {
            var pos = Vector2f.of(mouseEvent.getX(), mouseEvent.getY());
            var tilePos = terrainRenderer.getTilePosition(pos.div(bufferedRenderer.getScale()));
            if(tilePos != null) {
                var tile = terrain.getComponent(TerrainChunk.class).getTiles()[(int) tilePos.x()][(int) tilePos.y()];
                terrain.getComponent(TerrainChunk.class).getTiles()[(int) tilePos.x()][(int) tilePos.y()] = new Tile("1", 0);
                terrain.getComponent(TerrainChunk.class).markDirty(true);
                System.out.println("x:" + tilePos.x() + " y:" + tilePos.y());
            }
        });
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

        if(inputBuffer.isKeyClicked(KeyEvent.VK_1)) {
            windowProvider.resize(new Vector2f(800, 600));
        }else if(inputBuffer.isKeyClicked(KeyEvent.VK_2)) {
            windowProvider.resize(new Vector2f(1280, 720));
        }else if(inputBuffer.isKeyClicked(KeyEvent.VK_3)) {
            windowProvider.resize(new Vector2f(1920, 1080));
        }

        var position = camera.getComponent(Position.class);
        camera.setComponent(position.move(movement));

        hoverPos = InterpolateHelper.interpolateLinear(hoverPos, targetPos, 0.5f);

        canvas.render();
    }

    @Override
    public void cleanup() {

    }

    private void loadAssets(EngineContext context) {
        var assetLoader = context.<AssetLoader>getService(AssetLoader.class);
        assetLoader.load(Map.ofEntries(
                entry("test.png", LoadConfiguration.DefaultImage),
                entry("cursor.png", LoadConfiguration.DefaultImage)
        ));
    }

}
