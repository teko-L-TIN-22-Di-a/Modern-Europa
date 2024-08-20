package scenes.lib.components;

public record Combat(double cooldown, double damage, double speed) {
    public static Combat of(double damage, double speed) {
        return new Combat(0, damage, speed);
    }
    public Combat cooldown(double cooldown) {
        return new Combat(this.cooldown - cooldown, damage, speed);
    }
    public Combat attack(double initialCooldown) {
        return new Combat(initialCooldown, damage, speed);
    }
    public boolean ready() {
        return cooldown <= 0;
    }
}
