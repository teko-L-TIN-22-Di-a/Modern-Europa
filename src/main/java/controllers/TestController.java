package controllers;

import core.Controller;
import core.EngineContext;
import core.SleepHelper;
import core.graphics.JFrameWindowProvider;
import core.graphics.WindowProvider;
import core.loading.AssetLoader;
import core.loading.AssetManager;
import core.loading.AssetType;
import core.loading.LoadConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

public class TestController extends Controller {

    private JPanel canvas;
    private float test = 0;

    @Override
    public void init(EngineContext context) {

        var assetLoader = context.<AssetLoader>getService(AssetLoader.class);
        assetLoader.load("test.png", new LoadConfiguration(AssetType.Image));

        var assetManager = context.<AssetManager>getService(AssetManager.class);
        var testImage = assetManager.<BufferedImage>getAsset("test.png");

        var windowProvider = context.<WindowProvider>getService(WindowProvider.class);
        canvas = new JPanel() {
            @Override
            public void paint(Graphics g) {
                var graphics = (Graphics2D) g;

                var image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
                var random = new Random();

                for(int y = 0; y < image.getHeight(); y++) {
                    for(int x = 0; x < image.getWidth(); x++) {
                        int color = random.nextInt(255);
                        image.setRGB(x, y, new Color(color, color, color).getRGB());
                    }
                }

                graphics.drawImage(image, 0, 0, null);

                graphics.scale(5,5);

                graphics.drawImage(testImage, 0, 0, null);

                graphics.scale(1,1);

                graphics.setPaint(Color.BLACK);

                graphics.drawString("Hello World", test,50);
                test += 0.1;

                super.paint(g);
            }
        };
        windowProvider.addComponent(canvas);
        canvas.setOpaque(false);

        var btn = new JButton("Click Me");
        canvas.add(btn);

        var btn2 = new JButton("Click Me");
        canvas.add(btn2);

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
