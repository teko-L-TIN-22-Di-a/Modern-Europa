package rendering;

import core.EngineContext;
import core.ecs.Ecs;
import core.ecs.EcsView2;
import core.ecs.components.CameraComponent;
import core.ecs.components.PositionComponent;
import core.ecs.helper.CameraHelper;
import core.loading.AssetManager;
import core.loading.ImageFormatHelper;
import core.util.Vector2f;
import models.Tile;
import models.components.TerrainChunkComponent;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class IsometricTerrainRenderer implements Renderer {

    private final Ecs ecs;

    private final Map<Integer, RenderedChunkEntry> bufferedChunks = new HashMap<>();
    private BufferedImage testTile;

    public IsometricTerrainRenderer(EngineContext context) {
        ecs = context.getService(Ecs.class);

        var assetManager = context.<AssetManager>getService(AssetManager.class);
        testTile = assetManager.<BufferedImage>getAsset("test.png");
        ImageFormatHelper.keyOut(testTile, Color.WHITE);
    }

    @Override
    public void render(Graphics2D g2d) {
        var cameraEntries = ecs.view(CameraComponent.class, PositionComponent.class);
        var cameraOffset = getCameraPosition(cameraEntries);

        var terrainEntries = ecs.view(TerrainChunkComponent.class, PositionComponent.class);

        for (var chunkEntry : terrainEntries) {

            if(!bufferedChunks.containsKey(chunkEntry.entityId())) {
                bufferedChunks.put(chunkEntry.entityId(), null);
                regenerateBuffer(chunkEntry);
            }

            if(chunkEntry.component1().isDirty()) {
                regenerateBuffer(chunkEntry);
            }

            var renderedChunk = bufferedChunks.get(chunkEntry.entityId());
            var chunkOffset = chunkEntry.component2().position();

            // OriginOf (0,0) on the Image + camera + chunk position
            var originOffset = renderedChunk.coordinateOrigin().mul(Vector2f.of(-1,-1));
            var renderOffset = originOffset.add(cameraOffset.add(chunkOffset));

            g2d.drawImage(
                    renderedChunk.image(),
                    (int) renderOffset.x(),
                    (int) renderOffset.y(),
                    null);

            // Draw 0,0 coordinate
            g2d.setColor(Color.GREEN);
            g2d.drawOval((int) cameraOffset.x()-8, (int) cameraOffset.y()-8, 16, 16);

        }
    }

    private void regenerateBuffer(EcsView2<TerrainChunkComponent, PositionComponent> terrain) {
        var renderedChunk = bufferedChunks.get(terrain.entityId());

        if(renderedChunk == null) {
            bufferedChunks.put(terrain.entityId(), createImage(terrain));
        }

        var bufferedChunk = bufferedChunks.get(terrain.entityId());
        var image = bufferedChunk.image();
        var g2d = (Graphics2D) image.getGraphics();

        var terrainSize = terrain.component1().getSize();
        var tiles = terrain.component1().getTiles();

        g2d.clearRect(0, 0, image.getWidth(), image.getHeight());

        g2d.setColor(Color.RED);
        g2d.scale(1,1);
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());

        Tile tile;
        for(var x = 0; x < terrainSize.x(); x++) {
            for(var z = 0; z < terrainSize.y(); z++) {
                tile = tiles[x][z];

                var isometricPosition = IsometricHelper.toScreenSpace(x, 0, z);
                var drawingPos = bufferedChunk.coordinateOrigin()
                        .add(isometricPosition)
                        // 0,0 is in center of image offset with half the tile size.
                        .add(Vector2f.of(-IsometricHelper.HalfTileSize.x(), 0));

                g2d.drawImage(
                        testTile,
                        (int) drawingPos.x(),
                        (int) drawingPos.y(),
                        (int) (drawingPos.x() + IsometricHelper.TileSize.x()),
                        (int) (drawingPos.y() + IsometricHelper.TileSize.y()),
                        0,0,
                        (int) IsometricHelper.TileSize.x(),
                        (int) IsometricHelper.TileSize.y(),
                        null
                        );
            }
        }

        terrain.component1().markDirty(false);
    }

    private RenderedChunkEntry createImage(EcsView2<TerrainChunkComponent, PositionComponent> terrain) {
        var terrainSize = terrain.component1().getSize();
        int xWidth = (int) (terrainSize.x() * IsometricHelper.HalfTileSize.x());
        int zWidth = (int) (terrainSize.y() * IsometricHelper.HalfTileSize.x());

        var imageSize = Vector2f.of(
                xWidth + zWidth,
                (terrainSize.x() + terrainSize.y()) * IsometricHelper.HalfTileSize.y()
        );

        var image = ImageFormatHelper.newImage((int) imageSize.x(), (int) imageSize.y());

        var xOrigin = terrainSize.y() * IsometricHelper.HalfTileSize.x();

        return new RenderedChunkEntry(image, Vector2f.of(xOrigin, 0));
    }

    private Vector2f getCameraPosition(List<EcsView2<CameraComponent, PositionComponent>> cameras) {
        for (var entry : cameras) {
            if(entry.component1().active()) {
                return CameraHelper.GetCameraOffset(entry.component1(), entry.component2());
            }
        }

        return Vector2f.ZERO;
    }

}
