package scenes.lib.rendering;

import java.util.List;

public record Animation(
        List<AnimationFrame> frames,
        String leadingAnimation,
        boolean looping,
        float speed) {
}
