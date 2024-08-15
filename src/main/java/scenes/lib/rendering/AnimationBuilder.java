package scenes.lib.rendering;

import java.util.List;
import java.util.Map;

public class AnimationBuilder {

    private Map<String, Animation> animations;

    public static AnimationBuilder createAnimationAtlasEntry() {
        return new AnimationBuilder();
    }

    public AnimationBuilder addAnimation(String animationKey, String texture, List<Integer> frames) {

        addAnimation(
                animationKey,
                frames.stream().map(frame -> new AnimationFrame(texture + "_" + frame, 1)).toList(),
                null,
                false,
                1
        );

        return this;
    }

    public AnimationBuilder addAnimation(
            String animationKey,
            List<AnimationFrame> frames,
            String leadingAnimation,
            boolean looping,
            float speed) {

        animations.put(animationKey, new Animation(
                frames,
                leadingAnimation,
                looping,
                speed
        ));

        return this;
    }

    public AnimationAtlasEntry build() {
        return new AnimationAtlasEntry(animations, null);
    }

    public AnimationAtlasEntry build(String startingAnimation) {
        return new AnimationAtlasEntry(animations, startingAnimation);
    }

}
