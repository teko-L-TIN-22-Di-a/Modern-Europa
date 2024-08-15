package scenes.gamescene.playerstate;

import config.TileConfig;
import core.EngineContext;
import core.Parameters;
import core.ecs.Ecs;
import core.ecs.EcsView2;
import core.ecs.Entity;
import core.ecs.components.Position;
import core.input.InputBuffer;
import core.input.MouseListener;
import core.util.*;
import rx.Subscription;
import scenes.gamescene.RenderingContext;
import scenes.gamescene.commands.CommandConstants;
import scenes.lib.TextureConstants;
import scenes.lib.components.Sprite;
import scenes.lib.components.UnitInfo;
import scenes.lib.helper.EntityHelper;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PlaceState extends State {

    public static final String BUILDING_TYPE = "buildingType";

    private final List<Subscription> subscriptions = new ArrayList<>();

    private final Queue<MouseEvent> queuedMouseEvents = new ConcurrentLinkedQueue<>();

    private final Sprite highlightSprite;
    private final Sprite errorSprite;

    private final RenderingContext renderingContext;
    private final MouseListener mouseListener;
    private final InputBuffer inputBuffer;
    private final Ecs ecs;
    private final int playerId;

    private String buildingType;
    private Entity highlightEffectSprite;
    private Vector2f currPos = Vector2f.ZERO;
    private Vector2f targetPos = Vector2f.ZERO;
    private boolean placingEnabled = false;

    public PlaceState(EngineContext context, RenderingContext renderingContext, int playerId) {
        this.renderingContext = renderingContext;
        ecs = context.getService(Ecs.class);
        mouseListener = context.getService(MouseListener.class);
        inputBuffer = context.getService(InputBuffer.class);
        this.playerId = playerId;

        var spriteOffset = TileConfig.HalfTileSize;
        highlightSprite = new Sprite(TextureConstants.HIGHLIGHT, spriteOffset, true);
        errorSprite = new Sprite(TextureConstants.HIGHLIGHT_ERROR, spriteOffset, true);

    }

    @Override
    public void enter(Parameters parameters) {
        buildingType = parameters.getString(BUILDING_TYPE);

        renderingContext.selectionRenderer().setEnabled(false);
        renderingContext.mainGui().setEnabled(false);

        highlightEffectSprite = ecs.newEntity();
        highlightEffectSprite.setComponent(highlightSprite);

        subscriptions.addAll(List.of(
                mouseListener.bindMouseMoved(mouseEvent -> {
                    var pos = Vector2f.of(mouseEvent.getX(), mouseEvent.getY());
                    var tilePos = renderingContext.terrainRenderer().getTilePosition(pos.div(renderingContext.getScale()));
                    if(tilePos != null) {
                        targetPos = tilePos.add(0.5f, 0.5f);
                    }
                }),
                mouseListener.bindMouseReleased(queuedMouseEvents::add)
        ));
    }

    @Override
    public void update() {
        var units = ecs.view(Position.class, UnitInfo.class);

        var posFree = isTargetPosFree(units);
        var posNearUnit = isPosNearAlliedUnit(units);

        placingEnabled = posFree && posNearUnit;
        if(placingEnabled) {
            highlightEffectSprite.setComponent(highlightSprite);
        } else {
            highlightEffectSprite.setComponent(errorSprite);
        }

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
        if(!placingEnabled) return;

        EntityHelper.createCommand(ecs, CommandConstants.BUILDING_CREATION, new Parameters(Map.ofEntries(
                Map.entry(CommandConstants.BUILDING_CREATION_TYPE, buildingType),
                Map.entry(CommandConstants.BUILDING_CREATION_ID, UUID.randomUUID().toString()),
                Map.entry(CommandConstants.BUILDING_CREATION_POSITION, targetPos),
                Map.entry(CommandConstants.BUILDING_CREATION_PLAYER_ID, playerId)
        )));
        transitionTo(MainState.class);
    }

    private boolean isTargetPosFree(List<EcsView2<Position, UnitInfo>> units) {
        var blockingUnitTypes = Arrays.asList(
                UnitInfo.BASE,
                UnitInfo.GENERATOR,
                UnitInfo.MINER,
                UnitInfo.CONSTRUCTION_SITE
        );
        var blockingUnits = units.stream().filter(unit -> blockingUnitTypes.contains(unit.component2().type())).toList();

        for(var unit : blockingUnits) {

            var pos = unit.component1().position();
            var bounds = new Bounds(
                    Vector2f.of((float) Math.floor(pos.x()), (float) Math.floor(pos.z())),
                    Vector2f.of(1,1)
            );

            if(bounds.intersects(targetPos)) {
                return false;
            }

        }

        return true;
    }

    private boolean isPosNearAlliedUnit(List<EcsView2<Position, UnitInfo>> units) {
        var alliedUnits = units.stream().filter(unit -> unit.component2().playerId() == playerId).toList();

        for(var unit : alliedUnits) {
            var bounds = new CircleBounds(
                    unit.component1().position().toVector2fxz(),
                    unit.component2().visibilityStrength());

            if(bounds.intersects(targetPos)) {
                return true;
            }
        }

        return false;
    }

}