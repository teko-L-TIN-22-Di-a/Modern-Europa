package scenes.gamescene.PlayerState;

import config.TileConfig;
import core.EngineContext;
import core.Parameters;
import core.ecs.Ecs;
import core.ecs.Entity;
import core.ecs.components.Position;
import core.input.MouseListener;
import core.util.InterpolateHelper;
import core.util.State;
import core.util.Vector2f;
import rx.Subscription;
import scenes.gamescene.RenderingContext;
import scenes.gamescene.rendering.SelectionRenderer;
import scenes.lib.TextureConstants;
import scenes.lib.components.Sprite;
import scenes.lib.rendering.IsometricTerrainRenderer;

import java.util.ArrayList;
import java.util.List;

public class PlaceState extends State {

    private final List<Subscription> subscriptions = new ArrayList<>();

    private final RenderingContext renderingContext;
    private final MouseListener mouseListener;
    private final Ecs ecs;

    private Entity highlightEffectSprite;
    private Vector2f currPos = Vector2f.ZERO;
    private Vector2f targetPos = Vector2f.ZERO;

    public PlaceState(EngineContext context, RenderingContext renderingContext) {
        this.renderingContext = renderingContext;
        ecs = context.getService(Ecs.class);
        mouseListener = context.getService(MouseListener.class);
    }

    @Override
    public void enter(Parameters parameters) {
        renderingContext.selectionRenderer().setEnabled(false);

        highlightEffectSprite = ecs.newEntity();
        highlightEffectSprite.setComponent(new Sprite(TextureConstants.HIGHLIGHT, Vector2f.of(TileConfig.HalfTileSize.x(), 0), true));

        subscriptions.addAll(List.of(
                mouseListener.bindMouseMoved(mouseEvent -> {
                    // TODO add check if position is viable.
                    var pos = Vector2f.of(mouseEvent.getX(), mouseEvent.getY());
                    var tilePos = renderingContext.terrainRenderer().getTilePosition(pos.div(renderingContext.scale()));
                    if(tilePos != null) {
                        targetPos = tilePos;
                    }
                }),
                mouseListener.bindMouseClicked(mouseEvent -> {
                    // TODO call command
                    transitionBack();
                })
        ));
    }

    @Override
    public void update() {
        currPos = InterpolateHelper.interpolateLinear(currPos, targetPos, 0.5f);
        highlightEffectSprite.setComponent(new Position(currPos.toVector3fy(0.2f)));
    }

    @Override
    public void exit(Parameters parameters) {
        subscriptions.forEach(Subscription::unsubscribe);
        subscriptions.clear();

        renderingContext.selectionRenderer().setEnabled(true);

        if(highlightEffectSprite != null) {
            highlightEffectSprite.delete();
        }
    }

}