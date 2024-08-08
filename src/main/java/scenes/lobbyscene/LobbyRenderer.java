package scenes.lobbyscene;

import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import scenes.lib.PlayerInfo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class LobbyRenderer extends JPanel {

    private final PublishSubject<Void> startButtonClick = PublishSubject.create();
    private final PublishSubject<Void> backButtonClick = PublishSubject.create();

    private JPanel menuPanel;
    private JPanel playersContainer;

    public LobbyRenderer(boolean startButtonVisible) {
        menuPanel = new JPanel(new GridBagLayout());
        menuPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        var constraints = new GridBagConstraints();

        // Back Button
        constraints.insets = new Insets(0, 0, 0, 100);
        constraints.anchor = GridBagConstraints.WEST;
        var backButton = new JButton("Leave");
        backButton.addActionListener(e -> backButtonClick.onNext(null));
        menuPanel.add(backButton, constraints);

        // Title
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        var title = new JLabel("Lobby");
        title.setFont(new Font(title.getFont().getName(), Font.BOLD, 52));
        menuPanel.add(title, constraints);

        // PlayerList
        playersContainer = new JPanel();
        playersContainer.setLayout(new BoxLayout(playersContainer, BoxLayout.Y_AXIS));
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.weighty = 1;
        constraints.gridy = 2;
        menuPanel.add(playersContainer, constraints);

        if(startButtonVisible) {

            constraints.gridy = 3;
            constraints.anchor = GridBagConstraints.EAST;
            constraints.fill = GridBagConstraints.NONE;
            constraints.insets = new Insets(10, 0, 10, 0);
            var startButton = new JButton("Start");
            startButton.addActionListener(e -> startButtonClick.onNext(null));
            menuPanel.add(startButton, constraints);
        }

        add(menuPanel);
    }

    public void UpdatePlayerList(ArrayList<PlayerInfo> players) {
        playersContainer.removeAll();

        for (var player : players) {
            var displayName = player.name() + (player.isHost() ? " [Host]" : "");
            playersContainer.add(new JLabel(displayName));
        }

        playersContainer.revalidate();
        playersContainer.repaint();
    }

    public Subscription bindStartButtonClick(Action1<Void> action) {
        return startButtonClick.subscribe(action);
    }
    public Subscription bindBackButtonClick(Action1<Void> action) {
        return backButtonClick.subscribe(action);
    }

}
