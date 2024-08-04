package scenes.lib.rendering;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class NewRenderCanvas extends Canvas {
    protected static final Logger logger = LogManager.getLogger(NewRenderCanvas.class);

    private List<Renderer> renderSteps = new ArrayList<>();
    private BufferStrategy bufferStrategy;

    public void init() {
        createBufferStrategy(2);
        setIgnoreRepaint(true);
        bufferStrategy = getBufferStrategy();
    }

    public void render() {
        var g = bufferStrategy.getDrawGraphics();

        for(var step : renderSteps) {
            // Isolate graphics context so that scaling and other transformations won't propagate
            // down to the child components.
            var g2d = (Graphics2D) g.create();
            step.render(g2d);
        }
        g.dispose();

        bufferStrategy.show();
    }

    public NewRenderCanvas(List<Renderer> renderSteps)
    {
        setRenderSteps(renderSteps);
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

}
