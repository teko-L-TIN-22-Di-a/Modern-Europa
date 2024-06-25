package scenes.lobbyscene;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class LobbyRenderer extends JPanel {

    private JPanel menuPanel;
    private JPanel playersContainer;

    public LobbyRenderer() {
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

}
