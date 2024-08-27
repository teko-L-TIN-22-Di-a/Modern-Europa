package scenes.gamescene.rendering.gui;

import javax.swing.*;
import java.awt.*;

public abstract class GuiTab {

    protected JPanel panel = new JPanel(new GridBagLayout());

    public JPanel getPanel() {
        return panel;
    }

    public abstract void update();

}
