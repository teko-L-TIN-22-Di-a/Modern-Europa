package scenes;

import config.ScreenConfig;
import core.Controller;
import core.EngineContext;
import core.graphics.ImageHelper;
import core.graphics.WindowProvider;
import core.loading.*;
import scenes.lib.rendering.BufferedRenderer;
import scenes.lib.rendering.NewRenderCanvas;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class AnotherTestController extends Controller {

    private NewRenderCanvas canvas;

    @Override
    public void init(EngineContext context) {

        loadAssets(context);

        var assetManager = context.<AssetManager>getService(AssetManager.class);
        var testImage = assetManager.<BufferedImage>getAsset("test.png");
        ImageHelper.keyOut(testImage, Color.WHITE);

        var windowProvider = context.<WindowProvider>getService(WindowProvider.class);
        canvas = new NewRenderCanvas(java.util.List.of(
                new BufferedRenderer(context, ScreenConfig.ViewportSize, List.of(
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
    }

}
