package controllers;

import core.Controller;
import core.EngineContext;
import core.SleepHelper;
import core.ecs.Ecs;
import core.ecs.components.CameraComponent;
import core.graphics.WindowProvider;
import core.input.InputBuffer;
import core.loading.*;
import core.util.Vector2f;
import rendering.BufferedRenderer;
import rendering.RenderCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

public class TestController extends Controller {

    private RenderCanvas canvas;
    private float test = 0;
    private Vector2f offset = Vector2f.ZERO;

    @Override
    public void init(EngineContext context) {

        var ecs = new Ecs();
        var newEntity = ecs.newEntity();
        newEntity.setComponent(new CameraComponent(Vector2f.of(300, 240), true));

        var assetLoader = context.<AssetLoader>getService(AssetLoader.class);
        assetLoader.load("test.png", new LoadConfiguration(AssetType.Image));
        assetLoader.load("cursor.png", new LoadConfiguration(AssetType.Image));

        var assetManager = context.<AssetManager>getService(AssetManager.class);
        var testImage = assetManager.<BufferedImage>getAsset("test.png");
        ImageFormatHelper.keyOut(testImage, Color.WHITE);

        var cursorImage = assetManager.<BufferedImage>getAsset("cursor.png");
        ImageFormatHelper.keyOut(cursorImage, Color.WHITE);
        var toolkit = Toolkit.getDefaultToolkit();
        var cursor = toolkit.createCustomCursor(cursorImage, new Point(0, 0), "cursor");

        var windowProvider = context.<WindowProvider>getService(WindowProvider.class);
        canvas = new RenderCanvas(List.of(
                new BufferedRenderer(context, new Vector2f(300, 240), List.of(
                    g2d -> {

                        var image = new BufferedImage(300, 240, BufferedImage.TYPE_INT_RGB);
                        var random = new Random();

                        for(int y = 0; y < image.getHeight(); y++) {
                            for(int x = 0; x < image.getWidth(); x++) {
                                int color = random.nextInt(200, 255);
                                image.setRGB(x, y, new Color(color, color, color).getRGB());
                            }
                        }

                        g2d.drawImage(image, 0, 0, null);


                        for(var x = 0; x < 25; x++) {
                            for(var z = 0; z < 25; z++) {

                                var locX = (x * 31) + (z * -31);
                                var locY = (x * 15) + (z * 15);

                                /*g2d.setPaint(Color.RED);
                                g2d.drawRect(locX, locY, 62, 30);*/
                                g2d.drawImage(testImage,
                                        (int)offset.x() + locX,(int)offset.y() + locY,
                                        (int)offset.x() + locX + 62, (int)offset.y() + locY + 30,
                                        0, 0, 62, 0 + 30,
                                        null);
                            }
                        }

                        /* Stress Test still ran fairly fast so 1'000'000 cursor images
                            Shouldn't worry about performance yet.
                            Can still do occlusion testing.
                        for(int y = 0; y < 1000; y++) {
                            for(int x = 0; x < 1000; x++) {
                                g2d.drawImage(cursorImage, x*64, y*64, null);
                            }
                        }
                        */

                        g2d.setPaint(Color.BLACK);

                        g2d.drawString("Hello World", test,50);
                        test++;
                    }
                ))
        ));
        //canvas.setOpaque(false);
        canvas.setCursor(cursor);
        windowProvider.addComponent(canvas);

        var btn = new JButton("Click Me");
        canvas.add(btn);
        var btn2 = new JButton("Click Me");
        canvas.add(btn2);
        canvas.revalidate();

        var input = context.<InputBuffer>getService(InputBuffer.class);
        input.bindKeyPressed(keyEvent -> {
           switch(keyEvent.getKeyCode()) {

               case KeyEvent.VK_W:
                   offset = offset.add(0, -5);
                   break;
               case KeyEvent.VK_S:
                   offset = offset.add(0, 5);
                   break;
               case KeyEvent.VK_A:
                   offset = offset.add(-5, 0);
                   break;
               case KeyEvent.VK_D:
                   offset = offset.add(5, 0);
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

}
