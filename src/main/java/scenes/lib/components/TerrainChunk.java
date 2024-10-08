package scenes.lib.components;

import core.util.Vector2f;
import scenes.lib.MapInfo;
import scenes.lib.TextureConstants;

import java.util.Optional;

public record TerrainChunk(Tile[][] tiles, boolean isDirty) {

    public static TerrainChunk generate(Vector2f size) {
        var tiles = new Tile[(int)size.x()][(int)size.y()];

        for (int x = 0; x < tiles.length; x++) {
            for (int z = 0; z < tiles[x].length; z++) {
                tiles[x][z] = new Tile(TextureConstants.DEFAULT_GROUND,0);
            }
        }

        return new TerrainChunk(tiles, false);
    }

    public static TerrainChunk generate(Vector2f size, MapInfo mapInfo) {
        var tiles = new Tile[(int)size.x()][(int)size.y()];

        for (int x = 0; x < tiles.length; x++) {
            for (int z = 0; z < tiles[x].length; z++) {
                tiles[x][z] = new Tile(TextureConstants.DEFAULT_GROUND,0);
            }
        }

        for(var spot : mapInfo.mineSpots()) {
            if(spot.x() >= tiles.length || spot.y() >=  tiles[0].length ) {
                continue;
            }

            tiles[(int) spot.x()][(int) spot.y()] = new Tile(TextureConstants.MINEABLE_GROUND,0);
        }

        return new TerrainChunk(tiles, false);
    }

    public TerrainChunk markDirty(boolean dirty) {
        return new TerrainChunk(tiles, dirty);
    }

    public boolean isDirty() {
        return isDirty;
    }

    public Vector2f getSize() {
        return new Vector2f(tiles.length, tiles[0].length);
    }

    public Optional<Tile> getTile(int x, int z) {
        if(x >= tiles.length || z >= tiles[0].length) {
            return Optional.empty();
        }

        return Optional.of(tiles[x][z]);
    }

    public Tile[][] getTiles() {
        return tiles;
    }

}
