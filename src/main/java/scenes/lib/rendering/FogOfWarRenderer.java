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
import scenes.lib.components.Sprite;
import scenes.lib.components.TerrainChunk;
import scenes.lib.components.Visibility;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FogOfWarRenderer implements Renderer {

    private final Ecs ecs;
    private BufferedImage noiseTexture;
    private BufferedImage visibleFogOfWar;
    private final Map<Integer, FogOfWarChunkEntry> bufferedChunks = new HashMap<>();

    public FogOfWarRenderer(EngineContext context, Vector2f viewPort) {
        ecs = context.getService(Ecs.class);

        noiseTexture = ImageHelper.newImage(viewPort);
        visibleFogOfWar = ImageHelper.newImage(viewPort);
    }

    @Override
    public void render(Graphics2D g2d) {
        var cameraEntries = ecs.view(Camera.class, Position.class);
        var cameraOffset = getCameraOffset(cameraEntries);

        var visibilityEntries = ecs.view(Visibility.class, Position.class);

        var terrainEntries = ecs.view(TerrainChunk.class, Position.class);

        ImageHelper.drawWhiteNoise(noiseTexture, 20, 100);
        ImageHelper.cleanup(visibleFogOfWar);
        var fowGraphics = (Graphics2D) visibleFogOfWar.getGraphics();

        for (var terrainEntry : terrainEntries) {

            if(!bufferedChunks.containsKey(terrainEntry.entityId())) {
                bufferedChunks.put(terrainEntry.entityId(), null);
            }

            updateBuffer(terrainEntry, visibilityEntries);

            var chunk = bufferedChunks.get(terrainEntry.entityId());
            var chunkOffset = getChunkOffset(terrainEntry);

            // OriginOf (0,0) on the Image + camera + chunk position
            var renderOffset = cameraOffset.add(chunkOffset).sub(chunk.originOffset());

            fowGraphics.drawImage(
                    chunk.image(),
                    (int) renderOffset.x(),
                    (int) renderOffset.y(),
                    null);
        }

        fowGraphics.setComposite(AlphaComposite.SrcIn);
        fowGraphics.drawImage(noiseTexture, 0, 0, null);
        g2d.drawImage(visibleFogOfWar, 0, 0, null);

    }

    private void updateBuffer(EcsView2<TerrainChunk, Position> terrain
            , List<EcsView2<Visibility, Position>> visibilityEntries) {
        var renderedChunk = bufferedChunks.get(terrain.entityId());

        if(renderedChunk == null) {
            bufferedChunks.put(terrain.entityId(), createChunkEntry(terrain));
            renderedChunk = bufferedChunks.get(terrain.entityId()); // TODO Huh?
        }

        var chunkGraphics = (Graphics2D) renderedChunk.image().getGraphics();

        for(var entry : visibilityEntries) {
            chunkGraphics.setComposite(AlphaComposite.DstOut);

            var position = IsometricHelper.toScreenSpace(entry.component2().position());
            var drawingPos = position.add(renderedChunk.originOffset());

            var viewSize = entry.component1().strength()*32;

            chunkGraphics.fillOval(
                    (int) (drawingPos.x() - viewSize/2), (int) (drawingPos.y() - viewSize/2),
                    (int) viewSize, (int) viewSize);
        }

    }

    private FogOfWarChunkEntry createChunkEntry(EcsView2<TerrainChunk, Position> terrain) {
        var terrainSize = terrain.component1().getSize();
        int xWidth = (int) (terrainSize.x() * TileConfig.HalfTileSize.x());
        int zWidth = (int) (terrainSize.y() * TileConfig.HalfTileSize.x());

        var imageSize = Vector2f.of(
                xWidth + zWidth,
                (terrainSize.x() + terrainSize.y()) * TileConfig.HalfTileSize.y()
        );

        var image = ImageHelper.newImage((int) imageSize.x(), (int) imageSize.y());
        var g = (Graphics2D) image.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());

        var xOrigin = terrainSize.y() * TileConfig.HalfTileSize.x();

        return new FogOfWarChunkEntry(
                image,
                Vector2f.of(xOrigin, 0));
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
