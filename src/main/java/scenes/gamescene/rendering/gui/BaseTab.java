package scenes.gamescene.rendering.gui;

import core.ecs.Ecs;
import core.ecs.EcsView;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import scenes.lib.components.PlayerResources;
import scenes.lib.components.Powered;
import scenes.lib.components.Selection;
import scenes.lib.components.UnitInfo;

import javax.swing.*;
import java.util.List;

public class BaseTab extends GuiTab {
    private final Ecs ecs;
    private final int playerId;

    private final JLabel lifeLabel;
    private final JLabel poweredLabel;

    private final JButton mechButton;
    private final JButton ballButton;

    private final PublishSubject<Void> mechButtonClick = PublishSubject.create();
    private final PublishSubject<Void> ballButtonClick = PublishSubject.create();

    public BaseTab(Ecs ecs, int playerId) {
        this.ecs = ecs;
        this.playerId = playerId;

        // State Panel
        var groupPanel = new JPanel();
        groupPanel.setBorder(BorderFactory.createTitledBorder("State"));
        groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.Y_AXIS));

        lifeLabel = new JLabel();
        groupPanel.add(lifeLabel);

        var unit = getSelectedUnit();

        poweredLabel = new JLabel();
        groupPanel.add(poweredLabel);
        if(!hasPoweredComponent(unit)) {
            poweredLabel.setVisible(false);
        }

        panel.add(groupPanel);

        // Unit Panel
        var unitsPanel = new JPanel();
        unitsPanel.setBorder(BorderFactory.createTitledBorder("Units"));

        mechButton = new JButton("Mech [50]");
        mechButton.addActionListener(x -> mechButtonClick.onNext(null));
        unitsPanel.add(mechButton);
        ballButton = new JButton("Ball [25]");
        ballButton.addActionListener(x -> ballButtonClick.onNext(null));
        unitsPanel.add(ballButton);

        panel.add(unitsPanel);
    }

    @Override
    public void update() {
        var unit = getSelectedUnit();
        var playerResources = ecs.view(PlayerResources.class)
                .stream().filter(x -> x.component().playerId() == playerId)
                .findFirst();

        if(unit == null || playerResources.isEmpty()) {
            return;
        }

        lifeLabel.setText(getHealthString(unit.component()));

        if(hasPoweredComponent(unit)) {
            poweredLabel.setText(getPoweredString(ecs.getComponent(unit.entityId(), Powered.class)));
        }

        ballButton.setEnabled(playerResources.get().component().minerals() >= 25);
        mechButton.setEnabled(playerResources.get().component().minerals() >= 50);

    }

    public Subscription bindMechButtonClick(Action1<Void> action) {
        return mechButtonClick.subscribe(action);
    }
    public Subscription bindBallButtonClick(Action1<Void> action) {
        return ballButtonClick.subscribe(action);
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
