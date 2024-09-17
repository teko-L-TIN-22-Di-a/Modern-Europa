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
import scenes.lib.networking.messages.CommandMessage;

import javax.swing.*;
import java.util.List;

public class MainTab extends GuiTab {
    private final Ecs ecs;
    private final int playerId;

    private final JButton baseButton;
    private final JButton generatorButton;
    private final JButton minerButton;

    private final PublishSubject<Void> baseButtonClick = PublishSubject.create();
    private final PublishSubject<Void> generatorButtonClick = PublishSubject.create();
    private final PublishSubject<Void> minerButtonClick = PublishSubject.create();

    public MainTab(Ecs ecs, int playerId) {
        this.ecs = ecs;
        this.playerId = playerId;

        // Buildings Panel
        var unitsPanel = new JPanel();
        unitsPanel.setBorder(BorderFactory.createTitledBorder("Buildings"));

        baseButton = new JButton("Base [100]");
        baseButton.addActionListener(x -> baseButtonClick.onNext(null));
        unitsPanel.add(baseButton);
        generatorButton = new JButton("Generator [25]");
        generatorButton.addActionListener(x -> generatorButtonClick.onNext(null));
        unitsPanel.add(generatorButton);
        minerButton = new JButton("Miner [50]");
        minerButton.addActionListener(x -> minerButtonClick.onNext(null));
        unitsPanel.add(minerButton);

        panel.add(unitsPanel);
    }

    @Override
    public void update() {
        var playerResources = ecs.view(PlayerResources.class)
                .stream().filter(x -> x.component().playerId() == playerId)
                .findFirst();

        if(playerResources.isEmpty()) {
            return;
        }

        baseButton.setEnabled(playerResources.get().component().minerals() >= 100);
        generatorButton.setEnabled(playerResources.get().component().minerals() >= 25);
        minerButton.setEnabled(playerResources.get().component().minerals() >= 50);
    }

    public Subscription bindBaseButtonClick(Action1<Void> action) {
        return baseButtonClick.subscribe(action);
    }
    public Subscription bindGeneratorButtonClick(Action1<Void> action) {
        return generatorButtonClick.subscribe(action);
    }
    public Subscription bindMinerButtonClick(Action1<Void> action) {
        return minerButtonClick.subscribe(action);
    }

}
