package scenes.lib.rendering;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class OldRenderCanvas extends JComponent {
    protected static final Logger logger = LogManager.getLogger(OldRenderCanvas.class);

    private List<Renderer> renderSteps = new ArrayList<>();

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        for(var step : renderSteps) {
            // Isolate graphics context so that scaling and other transformations won't propagate
            // down to the child components.
            var g2d = (Graphics2D) g.create();
            step.render(g2d);
        }
    }

    public OldRenderCanvas(List<Renderer> renderSteps) {
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
