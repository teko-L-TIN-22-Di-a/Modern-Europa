package scenes.lib.components;

public record SpriteAnimation(
        String animationReference,
        String previousAnimation,
        String currentAnimation,
        double frameTime,
        Integer currentFrame) {

    public static SpriteAnimation of(String animationReference) {
        return new SpriteAnimation(
                animationReference,
                null,
                null,
                0,
                null
        );
    }

    public SpriteAnimation play(String newAnimation) {
        return new SpriteAnimation(
                animationReference,
                currentAnimation,
                newAnimation,
                0,
                null
        );
    }

    public SpriteAnimation updateState(Integer currentFrame, double frameTime) {
        return new SpriteAnimation(
                animationReference,
                previousAnimation,
                currentAnimation,
                frameTime,
                currentFrame
        );
    }

    public SpriteAnimation elapse(double elapsedTime) {
        return updateState(currentFrame, frameTime - elapsedTime);
    }

    public boolean hasElapsed() {
        return frameTime <= 0;
    }

}
