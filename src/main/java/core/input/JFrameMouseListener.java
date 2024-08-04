package core.input;

import core.EngineContext;
import core.EngineEventHooks;
import core.graphics.JFrameWindowProvider;
import core.graphics.WindowProvider;
import core.util.Vector2f;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class JFrameMouseListener implements
        MouseListener,
        MouseMotionListener,
        MouseWheelListener,
        MouseInputListener
{

    private final Queue<Component> trackedComponents = new ConcurrentLinkedQueue<>();

    private final PublishSubject<KeyEvent> keyPress = PublishSubject.create();
    private final PublishSubject<MouseEvent> onMouseDragged = PublishSubject.create();
    private final PublishSubject<MouseEvent> onMouseMoved = PublishSubject.create();
    private final PublishSubject<MouseEvent> onMouseClicked = PublishSubject.create();
    private final PublishSubject<MouseEvent> onMousePressed = PublishSubject.create();
    private final PublishSubject<MouseEvent> onMouseReleased = PublishSubject.create();
    private final PublishSubject<MouseEvent> onMouseEntered = PublishSubject.create();
    private final PublishSubject<MouseEvent> onMouseExited = PublishSubject.create();
    private final PublishSubject<MouseWheelEvent> onMouseWheelMoved = PublishSubject.create();

    @Override
    public void mouseDragged(MouseEvent e) {
        onMouseDragged.onNext(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        onMouseMoved.onNext(e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        onMouseClicked.onNext(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        onMousePressed.onNext(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        onMouseReleased.onNext(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        onMouseEntered.onNext(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        onMouseExited.onNext(e);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        onMouseWheelMoved.onNext(e);
    }

    @Override
    public Subscription bindMouseDragged(Action1<MouseEvent> action) {
        return onMouseDragged.subscribe(action);
    }

    @Override
    public Subscription bindMouseMoved(Action1<MouseEvent> action) {
        return onMouseMoved.subscribe(action);
    }

    @Override
    public Subscription bindMouseClicked(Action1<MouseEvent> action) {
        return onMouseClicked.subscribe(action);
    }

    @Override
    public Subscription bindMousePressed(Action1<MouseEvent> action) {
        return onMousePressed.subscribe(action);
    }

    @Override
    public Subscription bindMouseReleased(Action1<MouseEvent> action) {
        return onMouseReleased.subscribe(action);
    }

    @Override
    public Subscription bindMouseEntered(Action1<MouseEvent> action) {
        return onMouseEntered.subscribe(action);
    }

    @Override
    public Subscription bindMouseExited(Action1<MouseEvent> action) {
        return onMouseExited.subscribe(action);
    }

    @Override
    public Subscription bindMouseWheelMoved(Action1<MouseWheelEvent> action) {
        return onMouseWheelMoved.subscribe(action);
    }

    private void initFor(Component component) {

        trackedComponents.add(component);

        component.addMouseListener(this);
        component.addMouseWheelListener(this);
        component.addMouseMotionListener(this);

    }

    private void cleanup() {
        for (var component : trackedComponents) {
            component.removeMouseListener(this);
            component.removeMouseWheelListener(this);
            component.removeMouseMotionListener(this);
        }
        trackedComponents.clear();
    }

    public static EngineContext.Builder addToServices(EngineContext.Builder builder) {
        builder.addService(MouseListener.class, new JFrameMouseListener());
        return builder;
    }

    public static EngineContext init(EngineContext context) {

        var mouseListener = context.<MouseListener>getService(MouseListener.class);

        if(mouseListener == null) {
            throw new RuntimeException("No mouseListener found, make sure to call JFrameMouseListener.addToServices beforehand.");
        }

        if(!(mouseListener instanceof JFrameMouseListener instance)) {
            throw new RuntimeException("A different mouseListener was registered.");
        }

        var windowProvider = context.<WindowProvider>getService(WindowProvider.class);
        windowProvider.bindComponentAdd(instance::initFor);
        windowProvider.bindCleanup(unused -> instance.cleanup());

        return context;
    }

}
