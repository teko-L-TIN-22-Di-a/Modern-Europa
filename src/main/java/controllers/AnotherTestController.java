package controllers;

import core.Controller;
import core.EngineContext;
import core.ecs.Ecs;
import core.ecs.Entity;
import core.ecs.components.CameraComponent;
import core.ecs.components.PositionComponent;
import core.graphics.WindowProvider;
import core.input.InputBuffer;
import core.input.MouseListener;
import core.loading.*;
import core.util.Vector2f;
import models.components.TerrainChunkComponent;
import rendering.BufferedRenderer;
import rendering.NewRenderCanvas;
import rendering.IsometricTerrainRenderer;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.List;

public class AnotherTestController extends Controller {

    private WindowProvider windowProvider;
    private NewRenderCanvas canvas;

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

        windowProvider = context.getService(WindowProvider.class);
        canvas = new NewRenderCanvas(java.util.List.of(
                new BufferedRenderer(context, new Vector2f(300, 240), List.of(
                        g2d -> {
                            g2d.setColor(Color.WHITE);
                            g2d.fillRect(0, 0, 800, 600);

                            var buff = new BufferedImage(124, 60, BufferedImage.TYPE_INT_ARGB);
                            var buffG2d = (Graphics2D) buff.getGraphics();
                            buffG2d.setColor(Color.GREEN);
                            buffG2d.fillRect(0,0, 124, 60);

                            buffG2d.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_IN));

                            buffG2d.drawImage(testImage, 0, 0, null);

                            g2d.drawImage(buff, 0, 0, null);

                        }
                ))
        ));
        canvas.setCursor(cursor);
        windowProvider.addComponent(canvas);
        canvas.init();
    }

    @Override
    public void update() {
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
