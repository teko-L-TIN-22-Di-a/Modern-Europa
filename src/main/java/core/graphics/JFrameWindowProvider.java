package core.graphics;

import core.Engine;
import core.EngineContext;
import core.EngineEventHooks;
import core.util.Vector2f;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

import javax.security.auth.Subject;
import javax.swing.*;
import java.awt.*;

public class JFrameWindowProvider implements WindowProvider {
    protected static final Logger logger = LogManager.getLogger(JFrameWindowProvider.class);

    private PublishSubject<Vector2f> onResize = PublishSubject.<Vector2f>create();
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

    public JFrame getWindow() {
        return window;
    }

    @Override
    public void resize(Vector2f newSize) {
        window.setSize((int)newSize.x(), (int)newSize.y());
        logger.debug("Resized Window to {}x{}", newSize.x(), newSize.y());
        onResize.onNext(new Vector2f(newSize.x(), newSize.y()));
    }

    @Override
    public Subscription bindWindowResize(Action1<Vector2f> action) {
        return onResize.subscribe(action);
    }

    @Override
    public Vector2f getWindowSize() {
        var size = window.getSize();
        return new Vector2f(size.width, size.height);
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
