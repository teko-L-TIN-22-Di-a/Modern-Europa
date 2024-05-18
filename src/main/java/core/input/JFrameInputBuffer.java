package core.input;

import core.EngineContext;
import core.EngineEventHooks;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

// TODO make it so that it actually also buffers input instead of just redirect it.
public class JFrameInputBuffer implements InputBuffer {

    private final Queue<Integer> currentDownKeys = new ConcurrentLinkedQueue<>();
    private final Queue<Integer> previousDownKeys = new ConcurrentLinkedQueue<>();

    private final PublishSubject<KeyEvent> keyPress = PublishSubject.create();
    private final PublishSubject<KeyEvent> keyRelease = PublishSubject.create();

    private JFrameInputBuffer() {}

    private void init() {
        KeyboardFocusManager
                .getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(e -> {
                    var key = e.getKeyCode();

                    if(e.getID() == KeyEvent.KEY_PRESSED) {
                        if(!currentDownKeys.contains(key)) {
                            currentDownKeys.add(e.getKeyCode());
                        }

                        keyPress.onNext(e);
                    } else if(e.getID() == KeyEvent.KEY_RELEASED) {
                        if(previousDownKeys.contains(key)) {
                            currentDownKeys.remove(key);
                        }

                        keyRelease.onNext(e);
                    }

                    return false;
                });
    }

    private void update() {
        previousDownKeys.clear();
        previousDownKeys.addAll(currentDownKeys);
    }

    public Subscription bindKeyReleased(Action1<KeyEvent> action) {
        return keyRelease.subscribe(action);
    }
    public Subscription bindKeyPressed(Action1<KeyEvent> action) {
        return keyPress.subscribe(action);
    }

    @Override
    public boolean isKeyDown(int key) {
        return currentDownKeys.contains(key);
    }

    @Override
    public boolean isKeyUp(int key) {
        return !currentDownKeys.contains(key);
    }

    @Override
    public boolean isKeyClicked(int key) {
        return !previousDownKeys.contains(key) && currentDownKeys.contains(key);
    }

    @Override
    public boolean isKeyPressed(int key) {
        return previousDownKeys.contains(key) && currentDownKeys.contains(key);
    }

    @Override
    public boolean isKeyReleased(int key) {
        return previousDownKeys.contains(key) && !currentDownKeys.contains(key);
    }

    public static EngineContext.Builder addToServices(EngineContext.Builder builder) {
        builder.addService(InputBuffer.class, new JFrameInputBuffer());
        return builder;
    }

    public static EngineContext init(EngineContext context) {

        var inputBuffer = context.<InputBuffer>getService(InputBuffer.class);

        if(inputBuffer == null) {
            throw new RuntimeException("No inputBuffer found, make sure to call JFrameInputBuffer.addToServices beforehand.");
        }

        if(!(inputBuffer instanceof JFrameInputBuffer instance)) {
            throw new RuntimeException("A different inputBuffer was registered.");
        }

        var engineHooks = context.<EngineEventHooks>getService(EngineEventHooks.class);
        engineHooks.bindAfterUpdate(x -> instance.update());

        instance.init();

        return context;
    }
}
