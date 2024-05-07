package core;

import rx.Subscription;
import rx.functions.Action1;

public interface EngineEventHooks {

    Subscription bindSwitchController(Action1<Void> action);

}
