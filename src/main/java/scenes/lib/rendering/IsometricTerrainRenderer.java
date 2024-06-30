package scenes.lib.rendering;

import config.TileConfig;
import core.EngineContext;
import core.ecs.Ecs;
import core.ecs.EcsView2;
import core.ecs.components.Camera;
import core.ecs.components.Position;
import core.ecs.helper.CameraHelper;
import core.graphics.ImageHelper;
import core.util.Bounds;
import core.util.Vector2f;
import scenes.lib.components.Tile;
import scenes.lib.components.TerrainChunk;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class IsometricTerrainRenderer implements Renderer {

    private final Ecs ecs;

    private final TextureAtlas textureAtlas;
    private final boolean mouseMapEnabled;
    private final Map<Integer, RenderedChunkEntry> bufferedChunks = new HashMap<>();
    private Vector2f bufferedCameraOffset = Vector2f.ZERO;

    public IsometricTerrainRenderer(EngineContext context, TextureAtlas textureAtlas, boolean enableMouseMap) {
        ecs = context.getService(Ecs.class);
        this.textureAtlas = textureAtlas;
        mouseMapEnabled = enableMouseMap;
    }

    public Vector2f getTilePosition(Vector2f mousePos) {
        var terrainEntries = ecs.view(TerrainChunk.class, Position.class);

        for(var terrainEntry : terrainEntries) {
            var chunk = bufferedChunks.get(terrainEntry.entityId());
            if(chunk == null) continue;

            var targetPos = mousePos
                    .sub(bufferedCameraOffset)
                    .add(chunk.originOffset());
            var bounds = chunk.visualBounds();

            if(!bounds.inersects(targetPos)) continue;

            targetPos = targetPos.sub(getChunkOffset(terrainEntry));

            var id = chunk.mouseMap().getRGB((int) targetPos.x(), (int) targetPos.y());
            var tilePosition = chunk.idMap().get(id);
            if(tilePosition != null) return tilePosition.add(terrainEntry.component2().position().toVector2fxy());
        }

        return null;
    }

    @Override
    public void render(Graphics2D g2d) {
        var cameraEntries = ecs.view(Camera.class, Position.class);
        bufferedCameraOffset = getCameraOffset(cameraEntries);

        var terrainEntries = ecs.view(TerrainChunk.class, Position.class);

        for (var terrainEntry : terrainEntries) {

            if(!bufferedChunks.containsKey(terrainEntry.entityId())) {
                bufferedChunks.put(terrainEntry.entityId(), null);
                regenerateBuffer(terrainEntry);
            }

            if(terrainEntry.component1().isDirty()) {
                regenerateBuffer(terrainEntry);
            }

            var chunk = bufferedChunks.get(terrainEntry.entityId());
            var chunkOffset = getChunkOffset(terrainEntry);

            // OriginOf (0,0) on the Image + camera + chunk position
            var renderOffset = bufferedCameraOffset.add(chunkOffset).sub(chunk.originOffset());

            g2d.drawImage(
                    chunk.image(),
                    (int) renderOffset.x(),
                    (int) renderOffset.y(),
                    null);
        }
    }

    private void regenerateBuffer(EcsView2<TerrainChunk, Position> terrain) {
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

    private void renderChunk(EcsView2<TerrainChunk, Position> terrain) {
        var bufferedChunk = bufferedChunks.get(terrain.entityId());
        var image = bufferedChunk.image();
        var g2d = (Graphics2D) image.getGraphics();

        var terrainSize = terrain.component1().getSize();
        var tiles = terrain.component1().getTiles();

        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
        g2d.setComposite(AlphaComposite.SrcOver);

        Tile tile;
        for(var x = 0; x < terrainSize.x(); x++) {
            for(var z = 0; z < terrainSize.y(); z++) {
                tile = tiles[x][z];

                var isometricPosition = IsometricHelper.toScreenSpace(x, 0, z);
                var drawingPos = bufferedChunk.originOffset()
                        .add(isometricPosition)
                        // 0,0 is in center of image offset with half the tile size.
                        .add(Vector2f.of(-TileConfig.HalfTileSize.x(), 0));

                var tileConfiguration = textureAtlas.get(tile.resourcePath());

                g2d.drawImage(
                        tileConfiguration.image(),
                        (int) drawingPos.x(),
                        (int) drawingPos.y(),
                        (int) (drawingPos.x() + TileConfig.TileSize.x()),
                        (int) (drawingPos.y() + TileConfig.TileSize.y()),
                        (int) tileConfiguration.offset().x(), (int) tileConfiguration.offset().y(),
                        (int) (tileConfiguration.offset().x() + tileConfiguration.size().x()),
                        (int) (tileConfiguration.offset().y() + tileConfiguration.size().y()),
                        null
                );
            }
        }
    }

    private void renderChunkMouseMap(EcsView2<TerrainChunk, Position> terrain) {
        var bufferedChunk = bufferedChunks.get(terrain.entityId());
        var image = bufferedChunk.mouseMap();
        var g2d = (Graphics2D) image.getGraphics();

        var srcOverComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
        var dstInComposite = AlphaComposite.getInstance(AlphaComposite.DST_IN);

        var terrainSize = terrain.component1().getSize();
        var tiles = terrain.component1().getTiles();

        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
        g2d.setComposite(AlphaComposite.SrcOver);

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

                var tileConfiguration = textureAtlas.get(tile.resourcePath());

                // Set draw mode to only render background color where it intersects with tile.
                mouseMapGraphics.setComposite(dstInComposite);
                mouseMapGraphics.drawImage(
                        tileConfiguration.image(),
                        0,0,
                        (int) TileConfig.TileSize.x(), (int) TileConfig.TileSize.y(),
                        (int) tileConfiguration.offset().x(), (int) tileConfiguration.offset().y(),
                        (int) (tileConfiguration.offset().x() + tileConfiguration.size().x()),
                        (int) (tileConfiguration.offset().y() + tileConfiguration.size().y()),
                        null
                );

                var isometricPosition = IsometricHelper.toScreenSpace(x, 0, z);
                var drawingPos = bufferedChunk.originOffset()
                        .add(isometricPosition)
                        // 0,0 is in center of image offset with half the tile size.
                        .add(Vector2f.of(-TileConfig.HalfTileSize.x(), 0));

                g2d.drawImage(
                        tileMouseMap,
                        (int) drawingPos.x(),
                        (int) drawingPos.y(),
                        null
                );

                currentId++;
            }
        }
    }

    private RenderedChunkEntry createChunkEntry(EcsView2<TerrainChunk, Position> terrain) {
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
                new Bounds(getChunkOffset(terrain), imageSize));
    }

    private Vector2f getChunkOffset(EcsView2<TerrainChunk, Position> terrainOffset) {
        return IsometricHelper.toScreenSpace(terrainOffset.component2().position());
    }

    private Vector2f getCameraOffset(List<EcsView2<Camera, Position>> cameras) {
        for (var entry : cameras) {
            if(entry.component1().active()) {
                return CameraHelper.GetCameraOffset(entry.component1(), entry.component2());
            }
        }

        return Vector2f.ZERO;
    }

}
