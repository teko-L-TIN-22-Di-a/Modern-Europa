package scenes.lib.rendering;

import core.Parameters;
import scenes.lobbyscene.LobbyController;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Map;

import static java.util.Map.entry;

public class DialogRenderer extends JOptionPane {

    public DialogRenderer(Object messages, int messageType, int optionType) {
        super(
                messages,
                messageType,
                optionType,
                null
        );
    }

    public int showDialog(Component container, String title) {
        var dialog = createDialog(container, title);
        dialog.setIconImage(null);
        dialog.setVisible(true);

        if (getValue() instanceof Integer value) {
            return value;
        }

        return JOptionPane.CLOSED_OPTION;
    }

}
