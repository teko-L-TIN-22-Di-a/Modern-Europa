package scenes.gamescene;

import core.EngineContext;
import core.ecs.Ecs;
import core.ecs.EcsView3;
import core.ecs.Entity;
import core.ecs.components.Position;
import core.util.Bounds;
import core.util.StateMachine;
import core.util.Vector2f;
import rx.Subscription;
import rx.functions.Action1;
import scenes.gamescene.PlayerState.MainState;
import scenes.lib.components.Selection;
import scenes.lib.components.UnitInfo;
import scenes.lib.rendering.IsometricHelper;

import java.util.List;

public class PlayerHandler {

    private Ecs ecs;
    private final StateMachine state;
    private final int playerId;
    private Entity camera;

    public PlayerHandler(EngineContext context, RenderingContext renderingContext, int playerId) {
        ecs = context.getService(Ecs.class);
        this.playerId = playerId;
        state = new StateMachine(List.of(
                new MainState()
        ), MainState.class);

        renderingContext.selectionRenderer().bindBoundsSelection(this::onBoundsSelection);
        renderingContext.selectionRenderer().bindPointSelection(this::onPointSelection);
    }

    public void update() {
        state.update();
    }

    private void onBoundsSelection(Bounds bounds) {
        var selectableUnits = getSelectableUnits();
        deselectAll(selectableUnits);

        for (var unit : selectableUnits) {
            var pos = IsometricHelper.toScreenSpace(unit.component1().position());
            var intersects = unit.component3().bounds().move(pos).intersects(bounds);

            if(!intersects) continue;

            ecs.setComponent(unit.entityId(), unit.component3().select());
        }
    }
    private void onPointSelection(Vector2f point) {
        var selectableUnits = getSelectableUnits();
        deselectAll(selectableUnits);

        for (var unit : selectableUnits) {
            var pos = IsometricHelper.toScreenSpace(unit.component1().position());
            var intersects = unit.component3().bounds().move(pos).intersects(point);

            if(!intersects) continue;

            ecs.setComponent(unit.entityId(), unit.component3().select());
            return;
        }
    }

    private List<EcsView3<Position, UnitInfo, Selection>> getSelectableUnits() {
        var units = ecs.view(Position.class, UnitInfo.class, Selection.class);
        return units.stream().filter(unit -> unit.component2().playerId() == playerId).toList();
    }
    private void deselectAll(List<EcsView3<Position, UnitInfo, Selection>> selectableUnits) {
        for(var unit : selectableUnits) {
            ecs.setComponent(unit.entityId(), unit.component3().unselect());
        }
    }

}