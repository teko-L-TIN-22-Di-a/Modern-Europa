package scenes.gamescene.rendering;

import rx.functions.Action1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class MainGui extends JPanel {

    private JTabbedPane tabContainer;

    public MainGui(Canvas canvasComponent) {
        super(new GridBagLayout());

        var constraints = new GridBagConstraints();

        // Add Canvas
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.fill = GridBagConstraints.BOTH;
        add(canvasComponent, constraints);

        // Add TabbedPane
        tabContainer = new JTabbedPane();

        constraints.gridy = 1;
        constraints.weighty = 0.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.ipady = 100;
        add(tabContainer, constraints);
    }

    public JPanel createNewTab(String title, Map<String, Map<String, Action1<Void>>> groups) {
        var container = new JPanel(new GridBagLayout());

        for(var group : groups.keySet()) {
            var actions = groups.get(group);

            var groupPanel = new JPanel();
            groupPanel.setBorder(BorderFactory.createTitledBorder(group));

            for(var action : actions.keySet()) {
                var button = new JButton(action);
                button.addActionListener(e -> actions.get(action).call(null));
                groupPanel.add(button);
            }

            container.add(groupPanel);
        }

        tabContainer.addTab(title, container);

        return container;
    }

}
