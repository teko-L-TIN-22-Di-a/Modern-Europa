package scenes.gamescene;

import core.EngineContext;
import core.Parameters;
import core.ecs.Ecs;
import core.ecs.EcsView3;
import core.ecs.components.Position;
import core.input.InputBuffer;
import core.util.Bounds;
import core.util.StateMachine;
import core.util.Vector2f;
import scenes.gamescene.playerstate.MainState;
import scenes.gamescene.playerstate.PlaceState;
import scenes.gamescene.rendering.gui.BaseTab;
import scenes.gamescene.rendering.gui.GuiTab;
import scenes.gamescene.rendering.gui.MultiSelectionTab;
import scenes.gamescene.rendering.gui.UnitTab;
import scenes.lib.components.Selection;
import scenes.lib.components.UnitInfo;
import scenes.gamescene.rendering.IsometricHelper;

import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PlayerHandler {

    private final Queue<String> queuedBuildingEvents = new ConcurrentLinkedQueue<>();
    private final Queue<Boolean> selectionEvents = new ConcurrentLinkedQueue<>();

    private final Ecs ecs;
    private final InputBuffer inputBuffer;
    private final StateMachine state;
    private final RenderingContext renderingContext;
    private final int playerId;

    private GuiTab selectionTab = null;

    public PlayerHandler(EngineContext context, RenderingContext renderingContext, int playerId) {
        ecs = context.getService(Ecs.class);
        inputBuffer = context.getService(InputBuffer.class);
        this.renderingContext = renderingContext;
        this.playerId = playerId;
        state = new StateMachine(List.of(
                new MainState(context, renderingContext, playerId),
                new PlaceState(context, renderingContext, playerId)
        ), MainState.class);

        renderingContext.mainGui().createNewTab("Main", Map.of("Buildings", Map.ofEntries(
                Map.entry("Base [100]", x -> queuedBuildingEvents.add(UnitInfo.BASE)),
                Map.entry("Generator [25]", x -> queuedBuildingEvents.add(UnitInfo.GENERATOR)),
                Map.entry("Miner [50]", x -> queuedBuildingEvents.add(UnitInfo.MINER))
        )));

        renderingContext.selectionRenderer().bindBoundsSelection(this::onBoundsSelection);
        renderingContext.selectionRenderer().bindPointSelection(this::onPointSelection);
    }

    public void update() {
        var isPlaceState = state.getCurrentState() instanceof PlaceState;
        if(!isPlaceState && inputBuffer.isKeyReleased(KeyEvent.VK_ESCAPE)) {
            this.renderingContext.mainGui().setEscapeMenuVisible(true);
        }

        state.update();

        if(!queuedBuildingEvents.isEmpty()) {
            prepareBuildCommand(queuedBuildingEvents.poll());
        }

        updateMainGui();

        if(selectionTab != null) {
            selectionTab.update();
        }
    }

    private void prepareBuildCommand(String buildingType) {
        state.transitionTo(PlaceState.class, new Parameters(Map.of(
                PlaceState.BUILDING_TYPE, buildingType
        )));
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

    private void updateMainGui() {
        if(selectionEvents.isEmpty()) {
            return;
        }

        selectionEvents.clear();

        if(selectionTab != null) {
            renderingContext.mainGui().removeTab(selectionTab);
        }

        selectionTab = initSelectionTab();

        if(selectionTab != null) {
            renderingContext.mainGui().createNewTab("Selection", selectionTab);
        }
    }

    private GuiTab initSelectionTab() {
        var units = ecs.view(UnitInfo.class, Selection.class)
                .stream().filter(unit -> unit.component2().selected())
                .toList();

        if(units.isEmpty()) {
            return null;
        }

        if(units.size() == 1) {

            var unit = units.get(0);

            if(unit.component1().type().equals(UnitInfo.BASE)) {
                return new BaseTab(ecs, playerId);
            }

            return new UnitTab(ecs);
        }

        return new MultiSelectionTab(units.size());
    }

    private List<EcsView3<Position, UnitInfo, Selection>> getSelectableUnits() {
        var units = ecs.view(Position.class, UnitInfo.class, Selection.class);
        return units.stream().filter(unit -> unit.component2().playerId() == playerId).toList();
    }
    private void deselectAll(List<EcsView3<Position, UnitInfo, Selection>> selectableUnits) {
        for(var unit : selectableUnits) {
            ecs.setComponent(unit.entityId(), unit.component3().unselect());
        }
        selectionEvents.add(true);
    }

}