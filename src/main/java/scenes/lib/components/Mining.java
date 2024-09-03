package scenes.lib.components;

public record Mining(double cooldown, double gain, double speed) {
    public static Mining of(double gain, double speed) {
        return new Mining(0, gain, speed);
    }
    public Mining cooldown(double cooldown) {
        return new Mining(this.cooldown - cooldown, gain, speed);
    }
    public Mining mine(double initialCooldown) {
        return new Mining(initialCooldown, gain, speed);
    }
    public boolean ready() {
        return cooldown <= 0;
    }
}
