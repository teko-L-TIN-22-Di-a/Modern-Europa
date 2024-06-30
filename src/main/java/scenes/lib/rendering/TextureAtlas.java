package scenes.lib.rendering;

import java.util.HashMap;
import java.util.Map;

public class TextureAtlas {

    private Map<String, TextureAtlasEntry> tiles = new HashMap<String, TextureAtlasEntry>();

    public TextureAtlas add(Map<String, TextureAtlasEntry> tiles) {
        this.tiles.putAll(tiles);
        return this;
    }

    public TextureAtlas add(String key, TextureAtlasEntry textureAtlasEntry) {
        tiles.put(key, textureAtlasEntry);
        return this;
    }

    public TextureAtlasEntry get(String key) {
        return tiles.get(key);
    }

}
