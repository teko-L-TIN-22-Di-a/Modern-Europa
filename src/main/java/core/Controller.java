package core;

import java.io.Closeable;

public abstract class Controller {

    public abstract void init(EngineContext context);

    public abstract void run();

    public abstract void cleanup();

}
