package scenes.lib.rendering;

import core.util.Vector2f;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class TextureAtlas {

    private Map<String, TextureAtlasEntry> textures = new HashMap<String, TextureAtlasEntry>();

    public TextureAtlas add(Map<String, TextureAtlasEntry> tiles) {
        this.textures.putAll(tiles);
        return this;
    }

    public TextureAtlas add(String key, TextureAtlasEntry textureAtlasEntry) {
        textures.put(key, textureAtlasEntry);
        return this;
    }

    public TextureAtlas addSingleFrame(String alias, String sheetKey, int id) {
        var frameKey = getId(sheetKey, id);

        if(textures.containsKey(frameKey)) {
            textures.put(alias, textures.get(frameKey));
        }

        return this;
    }

    public TextureAtlas addSplit(String sheetKey, BufferedImage image, Vector2f size) {
        var xCount = Math.ceil(image.getWidth() / size.x());
        var yCount = Math.ceil(image.getHeight() / size.y());

        for(int y = 0; y < yCount; y++) {
            for(int x = 0; x < xCount; x++) {

                var id = (int) (y*xCount + x);
                textures.put(
                        getId(sheetKey, id),
                        new TextureAtlasEntry(image, size.mul(Vector2f.of(x, y)), size)
                );

            }
        }

        return this;
    }

    public TextureAtlasEntry get(String key) {
        return textures.get(key);
    }

    public static String getId(String sheetKey, int id) {
        return sheetKey + "_" + id;
    }

}
