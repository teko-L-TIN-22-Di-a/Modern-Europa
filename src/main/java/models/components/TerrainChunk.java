package models.components;

import core.util.Vector2f;
import models.Tile;

public class TerrainChunk {

    private Tile[][] tiles;
    private boolean isDirty;

    public TerrainChunk(Vector2f size) {
        tiles = new Tile[(int)size.x()][(int)size.y()];

        for (int x = 0; x < tiles.length; x++) {
            for (int z = 0; z < tiles[x].length; z++) {
                tiles[x][z] = new Tile("3",0);
            }
        }
    }

    public void markDirty(boolean dirty) {
        isDirty = dirty;
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
