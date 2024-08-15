package scenes.lib.components;

public record SpriteAnimation(
        String spriteSheet,
        String previousAnimation,
        String currentAnimation,
        int counter) {
}
