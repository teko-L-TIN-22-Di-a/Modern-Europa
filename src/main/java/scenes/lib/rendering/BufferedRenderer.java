package scenes.lib.rendering;

import core.EngineContext;
import core.graphics.ImageHelper;
import core.graphics.WindowProvider;
import core.util.Vector2f;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class BufferedRenderer implements Renderer {
    protected static final Logger logger = LogManager.getLogger(BufferedRenderer.class);

    private final Queue<Vector2f> queuedResizeEvent = new ConcurrentLinkedQueue<>();

    private Vector2f windowSize;
    private BufferedImage image;

    private List<Renderer> renderSteps = new ArrayList<>();

    public BufferedRenderer(EngineContext context, Vector2f viewSize, List<Renderer> renderSteps) {
        var windowProvider = context.<WindowProvider>getService(WindowProvider.class);
        windowSize = windowProvider.getWindowSize();
        windowProvider.bindWindowResize(queuedResizeEvent::add);

        setRenderSteps(renderSteps);
        initBuffer(viewSize);
    }

    public void setRenderSteps(List<Renderer> renderSteps) {
        this.renderSteps = renderSteps;
        logger.debug(
                "Initialised render steps: {}",
                renderSteps
                        .stream()
                        .map(x -> x.getClass().getSimpleName())
                        .collect(Collectors.joining(" -> "))
        );
    }

    @Override
    public void render(Graphics2D g2d) {

        while(!queuedResizeEvent.isEmpty()) {
            windowSize = queuedResizeEvent.poll();
            this.renderSteps.forEach(renderer -> renderer.setScale(getScale()));
        }

        var internalG2d = (Graphics2D)image.getGraphics();
        internalG2d.clearRect(0, 0, image.getWidth(), image.getHeight());

        for (var renderer : renderSteps) {
            var scopedG2d = (Graphics2D) internalG2d.create();
            renderer.render(scopedG2d);
            scopedG2d.dispose();
        }

        var scale = getScale();
        g2d.scale(scale.x(), scale.y());
        g2d.drawImage(image, 0, 0, null);
    }

    @Override
    public void setSize(Vector2f size) {
        // Do nothing
    }

    @Override
    public void setScale(Vector2f scale) {
        // Do nothing
    }

    public Vector2f getScale() {
        return windowSize.div(image.getWidth(), image.getHeight());
    }

    public void resize(Vector2f viewSize) {
        initBuffer(viewSize);
    }

    private void initBuffer(Vector2f size) {
        image = ImageHelper.newImage((int)size.x(), (int)size.y());
        this.renderSteps.forEach(renderer -> {
            renderer.setSize(size);
            renderer.setScale(getScale());
        });
    }

}
