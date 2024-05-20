package rendering;

import java.util.HashMap;
import java.util.Map;

public class TileSet {

    private Map<String, TileSetConfiguration> tiles = new HashMap<String, TileSetConfiguration>();

    public TileSet add(Map<String, TileSetConfiguration> tiles) {
        this.tiles.putAll(tiles);
        return this;
    }

    public TileSet add(String key, TileSetConfiguration tileSetConfiguration) {
        tiles.put(key, tileSetConfiguration);
        return this;
    }

    public TileSetConfiguration get(String key) {
        return tiles.get(key);
    }

}
