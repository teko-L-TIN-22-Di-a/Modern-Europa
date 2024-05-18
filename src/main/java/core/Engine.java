package core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

import javax.swing.*;

public class Engine implements ControllerSwitcher, EngineEventHooks {
    protected static final Logger logger = LogManager.getLogger(Engine.class);

    private final PublishSubject<Void> initController = PublishSubject.create();
    private final PublishSubject<Void> beforeUpdate = PublishSubject.create();
    private final PublishSubject<Void> afterUpdate = PublishSubject.create();

    private Controller currentController;
    private EngineContext context;
    private boolean isRunning = true;

    private Engine(Engine.Builder builder) {

        var contextBuilder = new EngineContext.Builder();
        contextBuilder.addService(ControllerSwitcher.class, this);
        contextBuilder.addService(EngineEventHooks.class, this);
        builder.configureServiceAction.call(contextBuilder);

        context = contextBuilder.build();
        builder.startupServiceAction.call(context);

        switchTo(builder.bootController);
    }

    public void run() throws Exception {
        logger.debug("Starting main game loop.");

        long now;
        while(isRunning) {
            now = System.nanoTime();

            beforeUpdate.onNext(null);
            currentController.update();
            afterUpdate.onNext(null);

            SleepHelper.SleepPrecise(60,System.nanoTime() - now);

            var frameTime = (System.nanoTime() - now)/1000000;
            if(frameTime > 16.5) {
                logger.debug("Game loop slowdown to {}ms", frameTime);
            }

        }
    }

    @Override
    public void switchTo(Controller controller) {
        switchTo(controller, Parameters.EMPTY);
    }

    @Override
    public void switchTo(Controller controller, Parameters parameters) {
        logger.debug("Switching to controller <{}>", controller.getClass().getName());

        if(currentController != null) {
            currentController.cleanup();
        }

        currentController = controller;
        initController.onNext(null);
        controller.init(context);
        logger.debug("Controller <{}> initialised", controller.getClass().getName());
    }

    @Override
    public Subscription bindInitController(Action1<Void> action) {
        return initController.subscribe(action);
    }

    @Override
    public Subscription bindBeforeUpdate(Action1<Void> action) {
        return beforeUpdate.subscribe(action);
    }

    @Override
    public Subscription bindAfterUpdate(Action1<Void> action) {
        return afterUpdate.subscribe(action);
    }

    public static class Builder {

        private Controller bootController;
        private Action1<EngineContext.Builder> configureServiceAction;
        private Action1<EngineContext> startupServiceAction;

        public Builder bootstrapController(Controller controller) {
            this.bootController = controller;
            return this;
        }

        public Builder configureServices(Action1<EngineContext.Builder> action) {
            configureServiceAction = action;
            return this;
        }

        public Builder startupServices(Action1<EngineContext> action) {
            startupServiceAction = action;
            return this;
        }

        public Engine build() throws Exception {

            if(bootController == null) {
                throw new Exception("BootController not set! [bootstrapController] is required!");
            }

            return new Engine(this);
        }

    }

}
