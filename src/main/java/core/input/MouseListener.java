package core.input;

import core.util.Vector2f;
import rx.Subscription;
import rx.functions.Action1;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public interface MouseListener {

    Subscription bindMouseDragged(Action1<MouseEvent> action);
    Subscription bindMouseMoved(Action1<MouseEvent> action);
    Subscription bindMouseClicked(Action1<MouseEvent> action);
    Subscription bindMousePressed(Action1<MouseEvent> action);
    Subscription bindMouseReleased(Action1<MouseEvent> action);
    Subscription bindMouseEntered(Action1<MouseEvent> action);
    Subscription bindMouseExited(Action1<MouseEvent> action);
    Subscription bindMouseWheelMoved(Action1<MouseWheelEvent> action);

}
