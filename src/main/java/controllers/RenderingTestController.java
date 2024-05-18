package controllers;

import core.Controller;
import core.EngineContext;
import core.SleepHelper;
import core.ecs.Ecs;
import core.ecs.Entity;
import core.ecs.components.CameraComponent;
import core.ecs.components.PositionComponent;
import core.graphics.WindowProvider;
import core.input.InputBuffer;
import core.loading.*;
import core.util.Vector2f;
import models.components.TerrainChunkComponent;
import rendering.BufferedRenderer;
import rendering.RenderCanvas;
import rendering.IsometricTerrainRenderer;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.List;

public class RenderingTestController extends Controller {

    private Ecs ecs;

    private RenderCanvas canvas;
    private Entity camera;

    @Override
    public void init(EngineContext context) {

        loadAssets(context);

        var assetManager = context.<AssetManager>getService(AssetManager.class);
        var testImage = assetManager.<BufferedImage>getAsset("test.png");
        ImageFormatHelper.keyOut(testImage, Color.WHITE);

        var cursorImage = assetManager.<BufferedImage>getAsset("cursor.png");
        ImageFormatHelper.keyOut(cursorImage, Color.WHITE);

        var toolkit = Toolkit.getDefaultToolkit();
        var cursor = toolkit.createCustomCursor(cursorImage, new Point(0, 0), "cursor");

        ecs = context.getService(Ecs.class);
        var mainTerrain = ecs.newEntity();
        mainTerrain.setComponent(new TerrainChunkComponent(Vector2f.of(5, 5)));

        camera = ecs.newEntity();
        // TODO take camera viewport from configuration.
        camera.setComponent(new CameraComponent(Vector2f.of(300, 240), true));

        var windowProvider = context.<WindowProvider>getService(WindowProvider.class);
        canvas = new RenderCanvas(java.util.List.of(
                new BufferedRenderer(context, new Vector2f(300, 240), List.of(
                        g2d -> {
                            g2d.setColor(Color.WHITE);
                            g2d.fillRect(0, 0, 800, 600);
                        },
                        new IsometricTerrainRenderer(context)
                ))
        ));
        canvas.setCursor(cursor);
        windowProvider.addComponent(canvas);

        var input = context.<InputBuffer>getService(InputBuffer.class);
        input.bindKeyPressed(keyEvent -> {

            var movement = Vector2f.ZERO;

            switch(keyEvent.getKeyCode()) {

                case KeyEvent.VK_W:
                    movement = movement.add(0, -5);
                    break;
                case KeyEvent.VK_S:
                    movement = movement.add(0, 5);
                    break;
                case KeyEvent.VK_A:
                    movement = movement.add(-5, 0);
                    break;
                case KeyEvent.VK_D:
                    movement = movement.add(5, 0);
                    break;

                case KeyEvent.VK_1:
                    windowProvider.resize(new Vector2f(800, 600));
                    break;
                case KeyEvent.VK_2:
                    windowProvider.resize(new Vector2f(1280, 720));
                    break;
                case KeyEvent.VK_3:
                    windowProvider.resize(new Vector2f(1920, 1080));
                    break;
            }

            var position = camera.getComponent(PositionComponent.class);
            camera.setComponent(position.move(movement));
        });
    }

    @Override
    public void update() {
        var now = System.nanoTime();

        canvas.repaint();

        SleepHelper.SleepPrecise(60, System.nanoTime() - now);
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
