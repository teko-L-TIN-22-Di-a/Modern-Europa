package core.graphics;

import core.util.Vector2f;
import rx.Subscription;
import rx.functions.Action1;

import java.awt.*;

public interface WindowProvider {

    void resize(Vector2f newSize);

    Subscription bindWindowResize(Action1<Vector2f> action);

    Vector2f getWindowSize();

    void addComponent(Component component);

}
