package scenes.gamescene.rendering.gui;

import javax.swing.*;
import java.awt.*;

public class MultiSelectionTab extends GuiTab {

    public MultiSelectionTab(int count) {
        var groupPanel = new JPanel();
        groupPanel.setBorder(BorderFactory.createTitledBorder("Selection"));
        groupPanel.add(new JLabel("Selected Units [" + count + "]"));

        panel.add(groupPanel);
    }

    @Override
    public void update() {
        // Do nothing.
    }
}
