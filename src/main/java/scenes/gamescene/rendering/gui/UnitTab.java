package scenes.gamescene.rendering.gui;

import core.EngineContext;
import core.ecs.Ecs;
import core.ecs.EcsView;
import core.ecs.EcsView2;
import scenes.lib.components.Powered;
import scenes.lib.components.Selection;
import scenes.lib.components.UnitInfo;

import javax.swing.*;
import java.util.List;

public class UnitTab extends GuiTab {

    private final Ecs ecs;

    private final JLabel lifeLabel;
    private final JLabel poweredLabel;

    public UnitTab(Ecs ecs) {
        this.ecs = ecs;

        var groupPanel = new JPanel();
        groupPanel.setBorder(BorderFactory.createTitledBorder("State"));

        lifeLabel = new JLabel();
        groupPanel.add(lifeLabel);

        var unit = getSelectedUnit();

        poweredLabel = new JLabel();
        groupPanel.add(poweredLabel);
        if(!hasPoweredComponent(unit)) {
            poweredLabel.setVisible(false);
        }

        panel.add(groupPanel);
    }

    @Override
    public void update() {
        var unit = getSelectedUnit();

        if(unit == null) {
            return;
        }

        lifeLabel.setText(getHealthString(unit.component()));

        if(hasPoweredComponent(unit)) {
            poweredLabel.setText(getPoweredString(ecs.getComponent(unit.entityId(), Powered.class)));
        }
    }

    private boolean hasPoweredComponent(EcsView<UnitInfo> unit) {
        var types = List.of(
                UnitInfo.BASE,
                UnitInfo.MINER
        );

        return types.contains(unit.component().type());
    }

    private EcsView<UnitInfo> getSelectedUnit() {
        var unit = ecs.view(UnitInfo.class, Selection.class)
                .stream().filter(entry -> entry.component2().selected())
                .findFirst();

        if(unit.isEmpty()) {
            return null;
        }

        return new EcsView<>(unit.get().entityId(), unit.get().component1());
    }

    private String getPoweredString(Powered powered) {
        return powered.powered() ? "Power: [on]" : "Power: [off]";
    }

    private String getHealthString(UnitInfo info) {
        return "Health: [" + info.health() + " | " + info.maxHealth() + "]";
    }

}
