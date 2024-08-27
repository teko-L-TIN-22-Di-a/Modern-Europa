package scenes.lib.components;

public record Mining(double cooldown, float gain, double speed) {
    public static Combat of(double gain, double speed) {
        return new Combat(0, gain, speed);
    }
    public Combat cooldown(double cooldown) {
        return new Combat(this.cooldown - cooldown, gain, speed);
    }
    public Combat mine(double initialCooldown) {
        return new Combat(initialCooldown, gain, speed);
    }
    public boolean ready() {
        return cooldown <= 0;
    }
}
