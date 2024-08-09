package scenes.gamescene.playerstate;

import core.EngineContext;
import core.Parameters;
import core.ecs.Ecs;
import core.ecs.EcsView3;
import core.ecs.components.Position;
import core.input.MouseListener;
import core.util.CircleBounds;
import core.util.State;
import core.util.Vector2f;
import rx.Subscription;
import scenes.gamescene.RenderingContext;
import scenes.gamescene.commands.CommandConstants;
import scenes.lib.components.Selection;
import scenes.lib.components.UnitInfo;
import scenes.lib.entities.EntityHelper;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MainState extends State {

    private final List<Subscription> subscriptions = new ArrayList<>();

    private final Queue<MouseEvent> queuedMouseEvents = new ConcurrentLinkedQueue<>();

    private final RenderingContext renderingContext;
    private final MouseListener mouseListener;
    private final Ecs ecs;

    private final int playerId;

    private JPanel temporaryTab = null;

    public MainState(EngineContext context, RenderingContext renderingContext, int playerId) {
        this.renderingContext = renderingContext;
        ecs = context.getService(Ecs.class);
        mouseListener = context.getService(MouseListener.class);
        this.playerId = playerId;
    }

    @Override
    public void update() {
        var selectedBases = getSelectedBases();

        if(selectedBases.isEmpty() && temporaryTab != null) {
            renderingContext.mainGui().removeTab(temporaryTab);
            temporaryTab = null;
        }

        if(!selectedBases.isEmpty() && temporaryTab == null) {
            temporaryTab = renderingContext.mainGui().createNewTab("Base", Map.ofEntries(
                    Map.entry("Units", Map.ofEntries(
                            Map.entry("Mech Unit [25]", x -> System.out.println("")),
                            Map.entry("Ball Unit [5]", x -> System.out.println(""))
                    ))
            ));
        }

        while(!queuedMouseEvents.isEmpty()) {
            handleMouseEvent(queuedMouseEvents.poll());
        }
    }

    @Override
    public void enter(Parameters parameters) {
        subscriptions.add(mouseListener.bindMouseReleased(queuedMouseEvents::add));
    }

    @Override
    public void exit(Parameters parameters) {
        subscriptions.forEach(Subscription::unsubscribe);
        subscriptions.clear();
    }

    private void handleMouseEvent(MouseEvent event) {
        if(event.getButton() != MouseEvent.BUTTON3) return;

        var pos = Vector2f.of(event.getX(), event.getY());
        var tilePos = renderingContext.terrainRenderer().getTilePosition(pos.div(renderingContext.scale()));
        if(tilePos != null) {
            setPathFindingTarget(tilePos);
        }
    }

    private void setPathFindingTarget(Vector2f target) {
        var selectedUnits = getMovableSelectedUnits();

        // Very simple poisson disk distribution.
        var rnd = new Random();
        var maxTries = 10;
        var tries = 0;
        var circleSize = 0.2f;
        List<CircleBounds> points = new ArrayList<>();

        while(points.size() < selectedUnits.size()) {
            var tmpPos = Vector2f.of(rnd.nextFloat(0.2f, 0.8f), rnd.nextFloat(0.2f, 0.8f));
            var tmpCircle = new CircleBounds(tmpPos, circleSize);

            if(points.stream().noneMatch(tmpCircle::intersects) || tries >= maxTries) {
                points.add(tmpCircle);
                tries = 0;
            } else {
                tries++;
            }
        }

        var i = 0;
        for(var unit: selectedUnits) {
            var subPos = points.get(i++).position();
            EntityHelper.createCommand(ecs, CommandConstants.MOVEMENT_TARGET, new Parameters(Map.ofEntries(
                    Map.entry(CommandConstants.MOVEMENT_TARGET_POSITION, target.add(subPos)),
                    Map.entry(CommandConstants.MOVEMENT_TARGET_UNIT, unit.component2().uuid())
            )));
        }
    }

    private List<EcsView3<Position, UnitInfo, Selection>> getMovableSelectedUnits() {
        var units = ecs.view(Position.class, UnitInfo.class, Selection.class);
        return units.stream()
                .filter(unit ->
                        unit.component2().playerId() == playerId
                                && unit.component2().movementSpeed() > 0
                                && unit.component3().selected()
                ).toList();
    }

    private List<EcsView3<Position, UnitInfo, Selection>> getSelectedBases() {
        var units = ecs.view(Position.class, UnitInfo.class, Selection.class);

        return units.stream()
                .filter(unit ->
                        unit.component2().playerId() == playerId
                                && unit.component3().selected()
                                && unit.component2().type().equals(UnitInfo.BASE)
                ).toList();
    }

}
