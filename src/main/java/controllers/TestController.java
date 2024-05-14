package controllers;

import core.Controller;
import core.EngineContext;
import core.SleepHelper;
import core.graphics.WindowProvider;
import core.input.InputBuffer;
import core.loading.AssetLoader;
import core.loading.AssetManager;
import core.loading.AssetType;
import core.loading.LoadConfiguration;
import core.util.Vector2f;
import rendering.BufferedRenderer;
import rendering.RenderCanvas;
import rendering.Renderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

public class TestController extends Controller {

    private RenderCanvas canvas;
    private float test = 0;

    @Override
    public void init(EngineContext context) {

        var assetLoader = context.<AssetLoader>getService(AssetLoader.class);
        assetLoader.load("test.png", new LoadConfiguration(AssetType.Image));

        var assetManager = context.<AssetManager>getService(AssetManager.class);
        var testImage = assetManager.<BufferedImage>getAsset("test.png");

        var windowProvider = context.<WindowProvider>getService(WindowProvider.class);
        canvas = new RenderCanvas(List.of(
                new BufferedRenderer(context, new Vector2f(300, 240), List.of(
                    g2d -> {
                        var image = new BufferedImage(300, 240, BufferedImage.TYPE_INT_RGB);
                        var random = new Random();

                        for(int y = 0; y < image.getHeight(); y++) {
                            for(int x = 0; x < image.getWidth(); x++) {
                                int color = random.nextInt(255);
                                image.setRGB(x, y, new Color(color, color, color).getRGB());
                            }
                        }

                        g2d.drawImage(image, 0, 0, null);

                        g2d.drawImage(testImage, 0,0, 64, 32, 0, 0, 64, 32, null);

                        g2d.setPaint(Color.BLACK);

                        g2d.drawString("Hello World", test,50);
                    }
                ))
        ));
        windowProvider.addComponent(canvas);
        canvas.setOpaque(false);

        var btn = new JButton("Click Me");
        canvas.add(btn);

        var btn2 = new JButton("Click Me");
        canvas.add(btn2);

        var input = context.<InputBuffer>getService(InputBuffer.class);
        input.bindKeyPressed(keyEvent -> {
            System.out.println(keyEvent);
           switch(keyEvent.getKeyCode()) {
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
        });

    }

    @Override
    public void update() throws Exception {
        var now = System.nanoTime();

        canvas.repaint();

        SleepHelper.SleepPrecise(60, System.nanoTime() - now);
    }

    @Override
    public void cleanup() {

    }

}
