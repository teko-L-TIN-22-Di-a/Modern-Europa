package core.input;

import rx.Subscription;
import rx.functions.Action1;

import java.awt.event.KeyEvent;

public interface InputBuffer {

    Subscription bindKeyReleased(Action1<KeyEvent> action);
    Subscription bindKeyPressed(Action1<KeyEvent> action);

}
