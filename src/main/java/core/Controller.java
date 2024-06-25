package core;

import java.io.Closeable;

public abstract class Controller {

    public abstract void init(EngineContext context, Parameters parameters);

    public abstract void update();

    public abstract void cleanup();

}
