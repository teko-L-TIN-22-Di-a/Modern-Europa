package scenes;

import config.ScreenConfig;
import core.Controller;
import core.EngineContext;
import core.Parameters;
import core.graphics.WindowProvider;
import core.util.Vector2f;
import scenes.lib.rendering.BufferedRenderer;
import scenes.lib.rendering.NewRenderCanvas;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PiController extends Controller {

    private float insideCircle = 0;
    private float outsideCircle = 0;
    private Random rnd = new Random();

    private Vector2f circleOffset = Vector2f.of(10, 10);
    private Vector2f circleSize = Vector2f.of(64, 64);
    private Vector2f halfSize = circleSize.div(Vector2f.of(2));
    private List<Vector2f> points = new ArrayList<>();

    private NewRenderCanvas canvas;

    @Override
    public void init(EngineContext context, Parameters parameters) {

        for(var i = 0; i < 1000; i++) {
            newPoint();
        }

        var windowProvider = context.<WindowProvider>getService(WindowProvider.class);
        canvas = new NewRenderCanvas(java.util.List.of(
                new BufferedRenderer(context, ScreenConfig.ViewportSize, List.of(
                        g2d -> {

                            g2d.setColor(Color.WHITE);
                            g2d.fillRect(0, 0, (int) ScreenConfig.ViewportSize.x(), (int) ScreenConfig.ViewportSize.y());

                            g2d.setColor(Color.BLACK);
                            g2d.drawRect(
                                    (int) circleOffset.x(), (int) circleOffset.y(),
                                    (int) circleSize.x(), (int) circleSize.y()
                            );

                            for(var point : points) {
                                g2d.fillRect(
                                        (int) (point.x() * halfSize.x() + circleOffset.x() + halfSize.x()),
                                        (int) (point.y() * halfSize.y() + circleOffset.y() + halfSize.y()),
                                        1, 1);
                            }

                            g2d.setColor(Color.RED);
                            g2d.drawOval(
                                    (int) circleOffset.x(), (int) circleOffset.y(),
                                    (int) circleSize.x(), (int) circleSize.y()
                            );

                            if(outsideCircle > 0) {
                                g2d.drawString(
                                        "4 * (" + insideCircle + " / " + (insideCircle + outsideCircle) + ") = "
                                                + 4 * (insideCircle / (insideCircle + outsideCircle)),
                                        10, 100);
                            }
                        }
                ))
        ));
        windowProvider.addComponent(canvas);
        canvas.init();

    }

    @Override
    public void update(double delta) {

        newPoint();
        canvas.render();
    }

    @Override
    public void cleanup() {

    }

    private void newPoint() {
        var newPoint = Vector2f.of(
                rnd.nextFloat() * 2 -1,
                rnd.nextFloat() * 2 -1);
        points.add(newPoint);

        if (Math.hypot(newPoint.x(), newPoint.y()) <= 1f) {
            insideCircle++;
        } else {
            outsideCircle++;
        }
    }

}
