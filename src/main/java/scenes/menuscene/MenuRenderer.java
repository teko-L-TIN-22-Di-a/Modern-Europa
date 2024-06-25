package scenes.menuscene;

import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MenuRenderer extends JPanel {

    private final PublishSubject<Void> hostButtonClick = PublishSubject.create();
    private final PublishSubject<Void> joinButtonClick = PublishSubject.create();
    private final PublishSubject<Void> freeModeButtonClick = PublishSubject.create();

    private JPanel menuPanel;
    private JPanel buttonContainer;

    public MenuRenderer() {
        menuPanel = new JPanel(new GridBagLayout());
        menuPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        var constraints = new GridBagConstraints();

        // Title
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        var title = new JLabel("Modern-Europa");
        title.setFont(new Font(title.getFont().getName(), Font.BOLD, 52));
        menuPanel.add(title, constraints);

        // Menu Buttons
        InitButtons();
        menuPanel.add(buttonContainer, constraints);

        add(menuPanel);
    }

    private void InitButtons() {
        var constraints = new GridBagConstraints();
        buttonContainer = new JPanel();
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        var hostButton = new JButton("Host");
        hostButton.addActionListener(e -> hostButtonClick.onNext(null));
        buttonContainer.add(hostButton, constraints);

        var joinButton = new JButton("Join");
        joinButton.addActionListener(e -> joinButtonClick.onNext(null));
        buttonContainer.add(joinButton, constraints);

        var freeModeButton = new JButton("Free mode");
        freeModeButton.addActionListener(e -> freeModeButtonClick.onNext(null));
        buttonContainer.add(freeModeButton, constraints);

        constraints.weighty = 1;
    }

    public Subscription bindHostButtonClick(Action1<Void> action) {
        return hostButtonClick.subscribe(action);
    }
    public Subscription bindJoinButtonClick(Action1<Void> action) {
        return joinButtonClick.subscribe(action);
    }
    public Subscription bindFreeModeButtonClick(Action1<Void> action) {
        return freeModeButtonClick.subscribe(action);
    }

}
