package core.input;

import core.EngineContext;
import core.EngineEventHooks;
import core.graphics.JFrameWindowProvider;
import core.graphics.WindowProvider;
import core.loading.AssetLoader;
import core.loading.AssetManager;
import core.loading.FileAssetLoader;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;

// TODO make it so that it actually also buffers input instead of just redirect it.
public class JFrameInputBuffer implements InputBuffer, KeyListener {

    private HashSet<KeyEvent> keySet;

    private final PublishSubject<KeyEvent> keyPress = PublishSubject.create();
    private final PublishSubject<KeyEvent> keyRelease = PublishSubject.create();

    private JFrameInputBuffer() {}

    private void init() {
        KeyboardFocusManager
                .getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(e -> {

                    if(e.getID() == KeyEvent.KEY_PRESSED) {
                        keyPress.onNext(e);
                    } else if(e.getID() == KeyEvent.KEY_RELEASED) {
                        keyRelease.onNext(e);
                    }

                    return false;
                });
    }

    private void update() {
        // TODO
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Do Nothing
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keyPress.onNext(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keyRelease.onNext(e);
    }

    public Subscription bindKeyReleased(Action1<KeyEvent> action) {
        return keyRelease.subscribe(action);
    }
    public Subscription bindKeyPressed(Action1<KeyEvent> action) {
        return keyPress.subscribe(action);
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
        engineHooks.bindBeforeUpdate(x -> instance.update());

        instance.init();

        return context;
    }
}
