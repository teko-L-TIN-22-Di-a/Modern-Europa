package scenes.lib.components;

import core.Parameters;

import java.util.UUID;

public record Command(String commandType, String commandId, Parameters parameters, boolean sent, boolean processed, int ttl) {

    public static final int TimeToLive = 60;

    public static Command create(String commandType) {
        return new Command(commandType, UUID.randomUUID().toString(), Parameters.EMPTY, false, false, TimeToLive);
    }
    public static Command create(String commandType, Parameters parameters) {
        return new Command(commandType, UUID.randomUUID().toString(), parameters, false, false, TimeToLive);
    }

    public Command setSent() {
        return new Command(commandType, commandId, parameters, true, processed, TimeToLive);
    }
    public Command setSent(boolean sent) {
        return new Command(commandType, commandId, parameters, sent, processed, TimeToLive);
    }

    public boolean isAlive() {
        return ttl <= 0;
    }

    public Command tick() {
        return new Command(commandType, commandId, parameters, sent, processed, ttl-1);
    }

    public Command setProcessed() {
        return new Command(commandType, commandId, parameters, sent, true, TimeToLive);
    }
    public Command setProcessed(boolean processed) {
        return new Command(commandType, commandId, parameters, sent, processed, TimeToLive);
    }
}
