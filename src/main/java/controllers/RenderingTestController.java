package controllers;

import config.ScreenConfig;
import config.TileConfig;
import core.Controller;
import core.EngineContext;
import core.ecs.Ecs;
import core.ecs.Entity;
import core.ecs.components.Camera;
import core.ecs.components.Position;
import core.graphics.ImageHelper;
import core.graphics.WindowProvider;
import core.input.InputBuffer;
import core.input.MouseListener;
import core.loading.*;
import core.util.Vector2f;
import models.Tile;
import models.components.TerrainChunk;
import rendering.*;

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

    @Override
    public void init(EngineContext context) {

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
        terrain.setComponent(new TerrainChunk(Vector2f.of(10, 10)));

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
                terrainRenderer
        ));
        canvas = new NewRenderCanvas(java.util.List.of(
            bufferedRenderer
        ));

        canvas.setCursor(cursor);
        windowProvider.addComponent(canvas);
        canvas.init();

        inputBuffer = context.getService(InputBuffer.class);
        var mouseListener = context.<MouseListener>getService(MouseListener.class);
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

        canvas.render();
    }

    @Override
    public void cleanup() {

    }

    private void loadAssets(EngineContext context) {
        var assetLoader = context.<AssetLoader>getService(AssetLoader.class);
        assetLoader.load("test.png", new LoadConfiguration(AssetType.Image));
        assetLoader.load("cursor.png", new LoadConfiguration(AssetType.Image));
    }

}
