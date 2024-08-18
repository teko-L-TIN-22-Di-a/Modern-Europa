package scenes.lib.rendering;

import java.util.HashMap;
import java.util.Map;

public class AnimationAtlas {

    Map<String, AnimationAtlasEntry> animationEntries = new HashMap<>();

    public void addEntry(String key, AnimationAtlasEntry entry) {
        animationEntries.put(key, entry);
    }

    public AnimationAtlasEntry getEntry(String key) {
        return animationEntries.get(key);
    }

}
