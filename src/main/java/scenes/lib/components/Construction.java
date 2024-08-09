package scenes.lib.components;

public record Construction(String type, double buildTime) {
    public Construction reduceTime(double timeElapsed) {
        return new Construction(type, buildTime - timeElapsed);
    }
}
