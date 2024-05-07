package core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import socket.IoClient;

import java.util.HashMap;
import java.util.Map;

public class EngineContext {
    protected static final Logger logger = LogManager.getLogger(EngineContext.class);

    private final Map<Class<?>, Object> serviceCollection;

    private EngineContext(Map<Class<?>, Object> serviceCollection) {
        this.serviceCollection = serviceCollection;
    }

    public <T> T getService(Class<?> key) {

        if(!serviceCollection.containsKey(key)) {
            return null;
        }

        //noinspection unchecked
        return (T) serviceCollection.get(key);
    }

    public static class Builder {

        private final Map<Class<?>, Object> serviceCollection = new HashMap<>();

        public Builder addService(Class<?> key, Object instance) {
            logger.debug("Added servivce of type <{}>", key.getName());
            serviceCollection.put(key, instance);
            return this;
        }

        public EngineContext build() {
            return new EngineContext(serviceCollection);
        }

    }

}
