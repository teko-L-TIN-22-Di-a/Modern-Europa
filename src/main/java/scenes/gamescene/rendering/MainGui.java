package scenes.gamescene.rendering;

import rx.functions.Action1;
import scenes.lib.rendering.DialogRenderer;

import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseMotionListener;
import java.util.Arrays;
import java.util.Map;

public class MainGui extends JPanel {

    private JInternalFrame escapeMenu;
    private JTabbedPane tabContainer;

    public MainGui(Canvas canvasComponent) {
        super(new GridBagLayout());

        var constraints = new GridBagConstraints();

        // Escape Menu
        constraints.gridx = 0;
        constraints.gridy = 0;
        escapeMenu = createEscapeMenu();
        add(escapeMenu, constraints);

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

    public void setEscapeMenuVisible(boolean visible) {
        escapeMenu.setVisible(visible);
        validate();
    }

    public JInternalFrame createEscapeMenu() {
        var escapeMenu = new JInternalFrame("Menu", false, true);
        escapeMenu.setLayout(new GridBagLayout());
        var constraints = new GridBagConstraints();
        escapeMenu.setFrameIcon(null);

        // Remove mouse listener to disable dragging functionality.
        // https://stackoverflow.com/questions/13783753/trying-to-disable-dragging-of-a-jinternalframe
        var ui = (BasicInternalFrameUI) escapeMenu.getUI();
        var northPane = ui.getNorthPane();
        var motionListeners = northPane.getListeners(MouseMotionListener.class);

        for (var listener: motionListeners) {
            northPane.removeMouseMotionListener(listener);
        }

        constraints.insets = new Insets(10, 10, 10, 10);
        escapeMenu.add(new JButton("Leave"), constraints);
        escapeMenu.setVisible(false);

        return escapeMenu;
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
