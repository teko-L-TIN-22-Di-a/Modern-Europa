package scenes.lib.gui;

import javax.swing.*;
import java.awt.*;

public class MainGui extends JPanel {

    private JTabbedPane tabContainer;
    private JPanel mainTab;

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

        // Add Tabbedpane
        tabContainer = new JTabbedPane();
        tabContainer.addTab("Main", initMainTab());

        constraints.gridy = 1;
        constraints.weighty = 0.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.ipady = 100;
        add(tabContainer, constraints);
    }

    private JPanel initMainTab() {
        mainTab = new JPanel(new GridBagLayout());
        var constraints = new GridBagConstraints();

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.VERTICAL;
        var buildingsPanel = new JPanel();
        mainTab.setBorder(BorderFactory.createTitledBorder("Buildings"));
        buildingsPanel.add(new JButton("Base"));

        mainTab.add(buildingsPanel, constraints);
        return mainTab;
    }

}
