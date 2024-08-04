package scenes.gamescene.playerstate;

import core.EngineContext;
import core.Parameters;
import core.ecs.Ecs;
import core.ecs.EcsView3;
import core.ecs.components.Position;
import core.input.MouseListener;
import core.util.State;
import core.util.Vector2f;
import rx.Subscription;
import scenes.gamescene.RenderingContext;
import scenes.lib.components.PathFindingTarget;
import scenes.lib.components.Selection;
import scenes.lib.components.UnitInfo;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class MainState extends State {

    private final List<Subscription> subscriptions = new ArrayList<>();

    private final RenderingContext renderingContext;
    private final MouseListener mouseListener;
    private final Ecs ecs;

    private final int playerId;

    public MainState(EngineContext context, RenderingContext renderingContext, int playerId) {
        this.renderingContext = renderingContext;
        ecs = context.getService(Ecs.class);
        mouseListener = context.getService(MouseListener.class);
        this.playerId = playerId;
    }

    @Override
    public void update() {
        // Do Nothing
    }

    @Override
    public void enter(Parameters parameters) {
        subscriptions.add(mouseListener.bindMouseReleased(mouseEvent -> {

            if(mouseEvent.getButton() != MouseEvent.BUTTON3) return;

            var pos = Vector2f.of(mouseEvent.getX(), mouseEvent.getY());
            var tilePos = renderingContext.terrainRenderer().getTilePosition(pos.div(renderingContext.scale()));
            if(tilePos != null) {
                setPathFindingTarget(tilePos.add(0.5f, 0.5f));
            }

        }));
    }

    @Override
    public void exit(Parameters parameters) {
        subscriptions.forEach(Subscription::unsubscribe);
        subscriptions.clear();
    }

    private void setPathFindingTarget(Vector2f target) {
        var selectedUnits = getMovableSelectedUnits();
        for(var unit: selectedUnits) {
            ecs.setComponent(unit.entityId(), new PathFindingTarget(target));
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

}
