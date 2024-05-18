package core.input;

import rx.Subscription;
import rx.functions.Action1;

import java.awt.event.KeyEvent;

public interface InputBuffer {

    Subscription bindKeyReleased(Action1<KeyEvent> action);
    Subscription bindKeyPressed(Action1<KeyEvent> action);

    boolean isKeyDown(int key);
    boolean isKeyUp(int key);
    boolean isKeyClicked(int key);
    boolean isKeyPressed(int key);
    boolean isKeyReleased(int key);

}
