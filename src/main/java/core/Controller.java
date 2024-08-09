package core;

import java.io.Closeable;

public abstract class Controller {

    public abstract void init(EngineContext context, Parameters parameters);

    /**
     * Controller update method will be called automatically by the engine according to fps configured.
     * There is a before and after Update hook that can be access using the EngineEventHooks Service.
     * @param delta will be the delta value of the last frame. Close to one if the frame took as long as it should.
     */
    public abstract void update(double delta);

    public abstract void cleanup();

}
