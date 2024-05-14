package core.graphics;

import core.Engine;
import core.EngineContext;
import core.EngineEventHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

public class JFrameWindowProvider implements WindowProvider {
    protected static final Logger logger = LogManager.getLogger(JFrameWindowProvider.class);

    private JFrame window;

    private void init() {
        // TODO make configurable
        window = new JFrame("Test");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setSize(800, 600);
        window.setVisible(true);
    }

    private void cleanup() {
        window.getContentPane().removeAll();
        logger.debug("Cleaned up JFrameWindowProvider");
    }

    @Override
    public void addComponent(Component component) {
        window.add(component);
    }

    public static EngineContext.Builder addToServices(EngineContext.Builder builder) {
        builder.addService(WindowProvider.class, new JFrameWindowProvider());

        return builder;
    }

    public static EngineContext initWindow(EngineContext context) {

        var windowProvider = context.<WindowProvider>getService(WindowProvider.class);

        if (windowProvider == null) {
            throw new RuntimeException("No WindowProvider found to initialise. Call addToServices first.");
        }

        if (!(windowProvider instanceof JFrameWindowProvider instance)) {
            throw new RuntimeException("A different WindowProvider was used please use JFrameWindowProvider.");
        }

        var engineHooks = context.<EngineEventHooks>getService(EngineEventHooks.class);
        engineHooks.bindInitController(x -> instance.cleanup());

        instance.init();

        return context;
    }
}
