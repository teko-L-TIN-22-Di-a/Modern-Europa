package scenes.lobbyscene;

import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class LobbyRenderer extends JPanel {

    private final PublishSubject<Void> startButtonClick = PublishSubject.create();

    private JPanel menuPanel;
    private JPanel playersContainer;

    public LobbyRenderer(boolean startButtonVisible) {
        menuPanel = new JPanel(new GridBagLayout());
        menuPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        var constraints = new GridBagConstraints();

        // Title
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
        menuPanel.add(playersContainer, constraints);

        if(startButtonVisible) {
            var startButton = new JButton("Start");
            startButton.addActionListener(e -> startButtonClick.onNext(null));
            menuPanel.add(startButton);
        }

        add(menuPanel);
    }

    public void UpdatePlayerList(ArrayList<String> players) {
        playersContainer.removeAll();

        for (var playerName : players) {
            playersContainer.add(new JLabel(playerName));
        }

        playersContainer.revalidate();
        playersContainer.repaint();
    }

    public Subscription bindStartButtonClick(Action1<Void> action) {
        return startButtonClick.subscribe(action);
    }

}
