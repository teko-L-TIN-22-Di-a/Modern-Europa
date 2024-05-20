package rendering;

import config.TileConfig;
import core.EngineContext;
import core.ecs.Ecs;
import core.ecs.EcsView2;
import core.ecs.components.CameraComponent;
import core.ecs.components.PositionComponent;
import core.ecs.helper.CameraHelper;
import core.loading.AssetManager;
import core.graphics.ImageHelper;
import core.util.Bounds;
import core.util.Vector2f;
import models.Tile;
import models.components.TerrainChunkComponent;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class IsometricTerrainRenderer implements Renderer {

    private final Ecs ecs;

    private final boolean mouseMapEnabled;
    private final Map<Integer, RenderedChunkEntry> bufferedChunks = new HashMap<>();
    private BufferedImage testTile;

    public IsometricTerrainRenderer(EngineContext context, boolean enableMouseMap) {
        ecs = context.getService(Ecs.class);
        mouseMapEnabled = enableMouseMap;

        var assetManager = context.<AssetManager>getService(AssetManager.class);
        testTile = assetManager.getAsset("test.png");
        ImageHelper.keyOut(testTile, Color.WHITE);
    }

    public Vector2f getTilePosition(Vector2f mousePos) {
        // TODO check if querying for entites is necessary.
        var cameraEntries = ecs.view(CameraComponent.class, PositionComponent.class);
        var cameraOffset = getCameraOffset(cameraEntries);

        var terrainEntries = ecs.view(TerrainChunkComponent.class, PositionComponent.class);

        for(var terrainEntry : terrainEntries) {
            var chunk = bufferedChunks.get(terrainEntry.entityId());
            if(chunk == null) continue;

            var targetPos = mousePos
                    .sub(cameraOffset)
                    .add(chunk.originOffset())
                    .add(terrainEntry.component2().position());
            var bounds = chunk.visualBounds();

            if(!bounds.inersects(targetPos)) continue;

            var id = chunk.mouseMap().getRGB((int) targetPos.x(), (int) targetPos.y());
            var tilePosition = chunk.idMap().get(id);
            if(tilePosition != null) return tilePosition;
        }

        return null;
    }

    @Override
    public void render(Graphics2D g2d) {
        var cameraEntries = ecs.view(CameraComponent.class, PositionComponent.class);
        var cameraOffset = getCameraOffset(cameraEntries);

        var terrainEntries = ecs.view(TerrainChunkComponent.class, PositionComponent.class);

        for (var terrainEntry : terrainEntries) {

            if(!bufferedChunks.containsKey(terrainEntry.entityId())) {
                bufferedChunks.put(terrainEntry.entityId(), null);
                regenerateBuffer(terrainEntry);
            }

            if(terrainEntry.component1().isDirty()) {
                regenerateBuffer(terrainEntry);
            }

            var chunk = bufferedChunks.get(terrainEntry.entityId());
            var chunkOffset = terrainEntry.component2().position();

            // OriginOf (0,0) on the Image + camera + chunk position
            var renderOffset = cameraOffset.add(chunkOffset).sub(chunk.originOffset());

            g2d.drawImage(
                    chunk.image(),
                    (int) renderOffset.x(),
                    (int) renderOffset.y(),
                    null);
        }
    }

    private void regenerateBuffer(EcsView2<TerrainChunkComponent, PositionComponent> terrain) {
        var renderedChunk = bufferedChunks.get(terrain.entityId());

        if(renderedChunk == null) {
            bufferedChunks.put(terrain.entityId(), createChunkEntry(terrain));
        }

        renderChunk(terrain);

        if(mouseMapEnabled) {
            renderChunkMouseMap(terrain);
        }

        terrain.component1().markDirty(false);
    }

    private void renderChunk(EcsView2<TerrainChunkComponent, PositionComponent> terrain) {
        var bufferedChunk = bufferedChunks.get(terrain.entityId());
        var image = bufferedChunk.image();
        var g2d = (Graphics2D) image.getGraphics();

        var terrainSize = terrain.component1().getSize();
        var tiles = terrain.component1().getTiles();

        //g2d.clearRect(0, 0, image.getWidth(), image.getHeight()); // TODO probably not needed?
        g2d.setColor(new Color(0,0,0,0));
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());

        Tile tile;
        for(var x = 0; x < terrainSize.x(); x++) {
            for(var z = 0; z < terrainSize.y(); z++) {
                tile = tiles[x][z];

                var isometricPosition = IsometricHelper.toScreenSpace(x, 0, z);
                var drawingPos = bufferedChunk.originOffset()
                        .add(isometricPosition)
                        // 0,0 is in center of image offset with half the tile size.
                        .add(Vector2f.of(-TileConfig.HalfTileSize.x(), 0));

                // TODO add proper tile drawing
                g2d.drawImage(
                        testTile,
                        (int) drawingPos.x(),
                        (int) drawingPos.y(),
                        (int) (drawingPos.x() + TileConfig.TileSize.x()),
                        (int) (drawingPos.y() + TileConfig.TileSize.y()),
                        0,0,
                        (int) TileConfig.TileSize.x(),
                        (int) TileConfig.TileSize.y(),
                        null
                );
            }
        }
    }

    private void renderChunkMouseMap(EcsView2<TerrainChunkComponent, PositionComponent> terrain) {
        var bufferedChunk = bufferedChunks.get(terrain.entityId());
        var image = bufferedChunk.mouseMap();
        var g2d = (Graphics2D) image.getGraphics();

        var srcOverComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
        var dstInComposite = AlphaComposite.getInstance(AlphaComposite.DST_IN);

        var terrainSize = terrain.component1().getSize();
        var tiles = terrain.component1().getTiles();

        //g2d.clearRect(0, 0, image.getWidth(), image.getHeight()); // TODO probably not needed?
        g2d.setColor(new Color(0,0,0,0));
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());

        var tileMouseMap = ImageHelper.newImage((int) TileConfig.TileSize.x(), (int) TileConfig.TileSize.y());
        var mouseMapGraphics = (Graphics2D) tileMouseMap.getGraphics();

        var currentId = new Color(0, 0, 0, 255).getRGB() + 1;

        Tile tile;
        for(var x = 0; x < terrainSize.x(); x++) {
            for(var z = 0; z < terrainSize.y(); z++) {
                tile = tiles[x][z];

                bufferedChunk.idMap().put(currentId, Vector2f.of(x,z));

                // Reset draw composite
                mouseMapGraphics.setComposite(srcOverComposite);

                // Set color of tile to id
                mouseMapGraphics.setColor(new Color(currentId));
                mouseMapGraphics.fillRect(0,0, tileMouseMap.getWidth(), tileMouseMap.getHeight());

                // Set draw mode to only render background color where it intersects with tile.
                mouseMapGraphics.setComposite(dstInComposite);
                mouseMapGraphics.drawImage(
                        testTile,
                        0,0,
                        (int) TileConfig.TileSize.x(), (int) TileConfig.TileSize.y(),
                        62,0,
                        (int) TileConfig.TileSize.x() + 62, (int) TileConfig.TileSize.y(),
                        null
                );

                var isometricPosition = IsometricHelper.toScreenSpace(x, 0, z);
                var drawingPos = bufferedChunk.originOffset()
                        .add(isometricPosition)
                        // 0,0 is in center of image offset with half the tile size.
                        .add(Vector2f.of(-TileConfig.HalfTileSize.x(), 0));

                // TODO add proper tile drawing
                g2d.drawImage(
                        tileMouseMap,
                        (int) drawingPos.x(),
                        (int) drawingPos.y(),
                        (int) (drawingPos.x() + TileConfig.TileSize.x()),
                        (int) (drawingPos.y() + TileConfig.TileSize.y()),
                        0,0,
                        (int) TileConfig.TileSize.x(),
                        (int) TileConfig.TileSize.y(),
                        null
                );

                currentId++;
            }
        }
    }

    private RenderedChunkEntry createChunkEntry(EcsView2<TerrainChunkComponent, PositionComponent> terrain) {
        var terrainSize = terrain.component1().getSize();
        int xWidth = (int) (terrainSize.x() * TileConfig.HalfTileSize.x());
        int zWidth = (int) (terrainSize.y() * TileConfig.HalfTileSize.x());

        var imageSize = Vector2f.of(
                xWidth + zWidth,
                (terrainSize.x() + terrainSize.y()) * TileConfig.HalfTileSize.y()
        );

        var image = ImageHelper.newImage((int) imageSize.x(), (int) imageSize.y());
        BufferedImage mouseMap = null;

        if(mouseMapEnabled) {
            mouseMap = ImageHelper.newImage((int) imageSize.x(), (int) imageSize.y());
        }

        var xOrigin = terrainSize.y() * TileConfig.HalfTileSize.x();

        return new RenderedChunkEntry(
                image,
                mouseMap,
                new HashMap<>(),
                Vector2f.of(xOrigin, 0),
                new Bounds(terrain.component2().position(), imageSize));
    }

    private Vector2f getCameraOffset(List<EcsView2<CameraComponent, PositionComponent>> cameras) {
        for (var entry : cameras) {
            if(entry.component1().active()) {
                return CameraHelper.GetCameraOffset(entry.component1(), entry.component2());
            }
        }

        return Vector2f.ZERO;
    }

}
