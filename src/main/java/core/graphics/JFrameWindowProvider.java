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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class JFrameWindowProvider implements WindowProvider, ComponentListener {
    protected static final Logger logger = LogManager.getLogger(JFrameWindowProvider.class);

    private PublishSubject<Vector2f> onResize = PublishSubject.create();
    private PublishSubject<Component> onComponentAdd = PublishSubject.create();
    private PublishSubject<Void> onCleanup = PublishSubject.create();

    private JFrame window;

    private void init(Action1<JFrame> configuration) {
        window = new JFrame("Window");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(true);
        window.setSize(800, 600);
        window.addComponentListener(this);
        configuration.call(window);

        window.setVisible(true);
    }

    private void cleanup() {
        window.getContentPane().removeAll();
        logger.debug("Cleaned up JFrameWindowProvider");
        onCleanup.onNext(null);
    }

    @Override
    public void setBorderless(boolean value) {
        // TODO dont know how to do this yet. Maybe over GraphicsDevice?
        window.setUndecorated(value);
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
    public Subscription bindComponentAdd(Action1<Component> action) {
        return onComponentAdd.subscribe(action);
    }
    @Override
    public Subscription bindCleanup(Action1<Void> action) {
        return onComponentAdd.subscribe();
    }

    @Override
    public Vector2f getWindowSize() {
        var size = window.getSize();
        return new Vector2f(size.width, size.height);
    }

    @Override
    public void addComponent(Component component) {
        window.add(component);
        window.revalidate();
        onComponentAdd.onNext(component);
    }

    public static EngineContext.Builder addToServices(EngineContext.Builder builder) {
        builder.addService(WindowProvider.class, new JFrameWindowProvider());

        return builder;
    }

    public static EngineContext initWindow(EngineContext context, Action1<JFrame> configuration) {

        var windowProvider = context.<WindowProvider>getService(WindowProvider.class);

        if (windowProvider == null) {
            throw new RuntimeException("No WindowProvider found to initialise. Call addToServices first.");
        }

        if (!(windowProvider instanceof JFrameWindowProvider instance)) {
            throw new RuntimeException("A different WindowProvider was used please use JFrameWindowProvider.");
        }

        var engineHooks = context.<EngineEventHooks>getService(EngineEventHooks.class);
        engineHooks.bindInitController(x -> instance.cleanup());

        try {
            SwingUtilities.invokeAndWait(() -> instance.init(configuration));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return context;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        resize(getWindowSize());
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        // Do nothing
    }

    @Override
    public void componentShown(ComponentEvent e) {
        // Do nothing
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        // Do nothing
    }
}
