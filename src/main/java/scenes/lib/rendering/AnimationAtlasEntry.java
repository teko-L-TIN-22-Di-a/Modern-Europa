package scenes.lib.rendering;

import java.util.List;
import java.util.Map;

public record AnimationAtlasEntry(Map<String, Animation> animations, String startingAnimation) {
}
