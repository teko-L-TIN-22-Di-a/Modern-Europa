package scenes.lobbyscene;

import core.Controller;
import core.ControllerSwitcher;
import core.EngineContext;
import core.Parameters;
import core.graphics.WindowProvider;
import core.loading.AssetManager;
import core.networking.IoClient;
import core.networking.IoServer;
import scenes.lib.AssetConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class LobbyController extends Controller {

    public static final String HOST_ON_PORT = "hostOnPort";
    public static final String HOST_ADDRESS = "hostAddress";

    private WindowProvider windowProvider;
    private ControllerSwitcher switcher;

    private JPanel menuContainer;
    private JPanel menuPanel;

    private Controller pendingController;

    @Override
    public void init(EngineContext context, Parameters parameters) {
        switcher = context.getService(ControllerSwitcher.class);

        var assetManager = context.<AssetManager>getService(AssetManager.class);
        var cursor = assetManager.<Cursor>getAsset(AssetConstants.CURSOR);
        windowProvider = context.getService(WindowProvider.class);

        menuContainer = new JPanel();
        menuPanel = new JPanel(new GridBagLayout());
        menuPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        var constraints = new GridBagConstraints();

        constraints.anchor = GridBagConstraints.NORTH;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        var title = new JLabel("Lobby");
        title.setFont(new Font(title.getFont().getName(), Font.BOLD, 52));
        menuPanel.add(title, constraints);

        var playersContainer = new JPanel();
        playersContainer.setLayout(new BoxLayout(playersContainer, BoxLayout.Y_AXIS));
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.weighty = 1;
        menuPanel.add(playersContainer, constraints);

        menuPanel.setCursor(cursor);
        menuContainer.add(menuPanel);
        windowProvider.addComponent(menuContainer);

        var hostOnPort = parameters.getString(LobbyController.HOST_ON_PORT);
        var hostAddress = parameters.getString(LobbyController.HOST_ADDRESS);

        if(hostOnPort != null) {
            playersContainer.add(new JLabel("Host"));
            playersContainer.revalidate();

            var server = new IoServer();
            AtomicInteger i = new AtomicInteger(1);
            server.bindConnect(socket -> {
                playersContainer.add(new JLabel("Player" + i.getAndIncrement()));
                playersContainer.revalidate();
            });
            try {
                server.StartListening();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if(hostAddress != null) {
            var client = new IoClient();
            try {
                client.connect(hostAddress);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    public void update() {
        // Do nothing
    }

    @Override
    public void cleanup() {
        // Do nothing
    }

}
