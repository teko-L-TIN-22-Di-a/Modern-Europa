package core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import socket.IoServer;

public class Engine implements ControllerSwitcher, EngineEventHooks {
    protected static final Logger logger = LogManager.getLogger(Engine.class);

    private final PublishSubject<Void> controllerSwitch = PublishSubject.create();
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

    public void run() {
        while(isRunning) {
            currentController.run();
        }
    }

    @Override
    public void switchTo(Controller controller) {
        logger.debug("Switching to controller <{}>", controller.getClass().getName());

        controllerSwitch.onNext(null);

        if(currentController != null) {
            currentController.cleanup();
        }

        controller.init(context);
        currentController = controller;
    }

    @Override
    public Subscription bindSwitchController(Action1<Void> action) {
        return controllerSwitch.subscribe(action);
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
