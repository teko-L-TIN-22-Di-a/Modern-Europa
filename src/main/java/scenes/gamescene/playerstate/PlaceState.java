package scenes.gamescene.playerstate;

import config.TileConfig;
import core.EngineContext;
import core.Parameters;
import core.ecs.Ecs;
import core.ecs.Entity;
import core.ecs.components.Position;
import core.input.InputBuffer;
import core.input.MouseListener;
import core.util.InterpolateHelper;
import core.util.State;
import core.util.Vector2f;
import rx.Subscription;
import scenes.gamescene.RenderingContext;
import scenes.gamescene.commands.CommandConstants;
import scenes.lib.TextureConstants;
import scenes.lib.components.Sprite;
import scenes.lib.entities.EntityHelper;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PlaceState extends State {

    public static final String BUILDING_TYPE = "buildingType";

    private final List<Subscription> subscriptions = new ArrayList<>();

    private final Queue<MouseEvent> queuedMouseEvents = new ConcurrentLinkedQueue<>();

    private final RenderingContext renderingContext;
    private final MouseListener mouseListener;
    private final InputBuffer inputBuffer;
    private final Ecs ecs;
    private final int playerId;

    private String buildingType;
    private Entity highlightEffectSprite;
    private Vector2f currPos = Vector2f.ZERO;
    private Vector2f targetPos = Vector2f.ZERO;

    public PlaceState(EngineContext context, RenderingContext renderingContext, int playerId) {
        this.renderingContext = renderingContext;
        ecs = context.getService(Ecs.class);
        mouseListener = context.getService(MouseListener.class);
        inputBuffer = context.getService(InputBuffer.class);
        this.playerId = playerId;
    }

    @Override
    public void enter(Parameters parameters) {
        buildingType = parameters.getString(BUILDING_TYPE);

        renderingContext.selectionRenderer().setEnabled(false);
        renderingContext.mainGui().setEnabled(false);

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
                mouseListener.bindMouseReleased(queuedMouseEvents::add)
        ));
    }

    @Override
    public void update() {
        currPos = InterpolateHelper.interpolateLinear(currPos, targetPos, 0.5f);
        highlightEffectSprite.setComponent(new Position(currPos.toVector3fy(0.2f)));

        if(inputBuffer.isKeyDown(KeyEvent.VK_ESCAPE)) {
            transitionTo(MainState.class);
        }

        while(!queuedMouseEvents.isEmpty()) {
            handleMouseEvent(queuedMouseEvents.poll());
        }
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

    private void handleMouseEvent(MouseEvent event) {
        EntityHelper.createCommand(ecs, CommandConstants.BUILDING_CREATION, new Parameters(Map.ofEntries(
                Map.entry(CommandConstants.BUILDING_CREATION_TYPE, buildingType),
                Map.entry(CommandConstants.BUILDING_CREATION_ID, UUID.randomUUID().toString()),
                Map.entry(CommandConstants.BUILDING_CREATION_POSITION, targetPos.add(0.5f, 0.5f)),
                Map.entry(CommandConstants.BUILDING_CREATION_PLAYER_ID, playerId)
        )));
        transitionTo(MainState.class);
    }

}