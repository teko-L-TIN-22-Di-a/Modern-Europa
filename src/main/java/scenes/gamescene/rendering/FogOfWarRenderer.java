package scenes.gamescene.rendering;

import scenes.lib.config.RenderingConfig;
import core.EngineContext;
import core.ecs.Ecs;
import core.ecs.EcsView2;
import core.ecs.components.Position;
import scenes.lib.helper.CameraHelper;
import core.graphics.ImageHelper;
import core.util.Vector2f;
import scenes.lib.components.TerrainChunk;
import scenes.lib.components.UnitInfo;
import scenes.lib.rendering.Renderer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FogOfWarRenderer implements Renderer {

    private final Map<Integer, BufferedImage> bufferedFogOfWarBrushes = new HashMap<>();

    private final Ecs ecs;
    private BufferedImage noiseTexture;
    private BufferedImage visibleFogOfWar;
    private final Map<Integer, FogOfWarChunkEntry> bufferedChunks = new HashMap<>();
    private final int playerIdFilter;
    private Vector2f viewPort = Vector2f.ZERO;

    public FogOfWarRenderer(EngineContext context, int playerIdFilter) {
        ecs = context.getService(Ecs.class);

        this.playerIdFilter = playerIdFilter;
        initBuffers(Vector2f.of(1));
    }

    @Override
    public void render(Graphics2D g2d) {
        var cameraOffset = CameraHelper.getCameraOffset(ecs);

        var visibilityEntries = ecs.view(Position.class, UnitInfo.class);

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

    @Override
    public void setSize(Vector2f size) {
        this.viewPort = size;
        initBuffers(size);
    }

    @Override
    public void setScale(Vector2f scale) {
        // Do nothing.
    }

    private void initBuffers(Vector2f size) {
        noiseTexture = ImageHelper.newImage(size);
        visibleFogOfWar = ImageHelper.newImage(size);
    }

    private void updateBuffer(EcsView2<TerrainChunk, Position> terrain
            , List<EcsView2<Position, UnitInfo>> visibilityEntries) {
        var renderedChunk = bufferedChunks.get(terrain.entityId());

        if(renderedChunk == null) {
            bufferedChunks.put(terrain.entityId(), createChunkEntry(terrain));
            renderedChunk = bufferedChunks.get(terrain.entityId());
        }

        resetFogOfWarEntry(renderedChunk);

        var chunkGraphics = (Graphics2D) renderedChunk.image().getGraphics();
        chunkGraphics.setComposite(AlphaComposite.DstOut);

        for(var entry : visibilityEntries) {

            if(entry.component2().playerId() != playerIdFilter) { continue; }

            var position = IsometricHelper.toScreenSpace(entry.component1().position());
            var drawingPos = position.add(renderedChunk.originOffset());

            var brush = getBufferedFogOfWarBrush(entry.component2().visibilityStrength());

            chunkGraphics.drawImage(
                    brush,
                    (int) (drawingPos.x() - brush.getWidth()/2f), (int) (drawingPos.y() - brush.getHeight()/2f),
                    null
            );
        }
    }

    private BufferedImage getBufferedFogOfWarBrush(int strength) {
        if(bufferedFogOfWarBrushes.containsKey(strength)) {
            return bufferedFogOfWarBrushes.get(strength);
        }

        var viewSize = RenderingConfig.TILE_SIZE.mul(strength);
        var newBrushImage = ImageHelper.newImage(viewSize);
        ImageHelper.cleanup(newBrushImage);
        var brushGraphics = (Graphics2D) newBrushImage.getGraphics();
        brushGraphics.fillOval(0, 0, newBrushImage.getWidth(), newBrushImage.getHeight());

        bufferedFogOfWarBrushes.put(strength, newBrushImage);
        return newBrushImage;
    }

    private FogOfWarChunkEntry createChunkEntry(EcsView2<TerrainChunk, Position> terrain) {
        var terrainSize = terrain.component1().getSize();
        int xWidth = (int) (terrainSize.x() * RenderingConfig.HALF_TILE_SIZE.x());
        int zWidth = (int) (terrainSize.y() * RenderingConfig.HALF_TILE_SIZE.x());

        var imageSize = Vector2f.of(
                xWidth + zWidth,
                (terrainSize.x() + terrainSize.y()) * RenderingConfig.HALF_TILE_SIZE.y()
        );

        var image = ImageHelper.newImage((int) imageSize.x(), (int) imageSize.y());
        var g = (Graphics2D) image.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());

        var xOrigin = terrainSize.y() * RenderingConfig.HALF_TILE_SIZE.x();

        return new FogOfWarChunkEntry(
                image,
                Vector2f.of(xOrigin, 0));
    }

    private void resetFogOfWarEntry(FogOfWarChunkEntry entry) {
        var g = (Graphics2D) entry.image().getGraphics();
        g.setColor(Color.BLACK);
        // This is really cool but also makes everything slower.
        //g.setColor(new Color(0,0,0, 0.1f));
        g.fillRect(0, 0, entry.image().getWidth(), entry.image().getHeight());
    }

    private Vector2f getChunkOffset(EcsView2<TerrainChunk, Position> terrainOffset) {
        return IsometricHelper.toScreenSpace(terrainOffset.component2().position());
    }

}
