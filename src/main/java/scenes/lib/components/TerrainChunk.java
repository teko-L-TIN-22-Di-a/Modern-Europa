package scenes.lib.components;

import core.util.Vector2f;

public record TerrainChunk(Tile[][] tiles, boolean isDirty) {

    public static TerrainChunk generate(Vector2f size) {
        var tiles = new Tile[(int)size.x()][(int)size.y()];

        for (int x = 0; x < tiles.length; x++) {
            for (int z = 0; z < tiles[x].length; z++) {
                tiles[x][z] = new Tile("1",0);
            }
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

    public Tile[][] getTiles() {
        return tiles;
    }

}
