package controllers;

import core.Controller;
import core.EngineContext;
import core.SleepHelper;
import core.graphics.JFrameWindowProvider;
import core.graphics.WindowProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class TestController extends Controller {

    private JPanel canvas;

    @Override
    public void init(EngineContext context) {

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
    public void run() throws Exception {
        canvas.repaint();
        SleepHelper.SleepPrecise( 1000d / 60);
    }

    @Override
    public void cleanup() {

    }

}
