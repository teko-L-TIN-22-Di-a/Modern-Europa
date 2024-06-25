package scenes.lobbyscene;

import core.Controller;
import core.ControllerSwitcher;
import core.EngineContext;
import core.Parameters;
import core.graphics.WindowProvider;
import core.loading.AssetManager;
import core.loading.Settings;
import core.networking.IoClient;
import core.networking.IoServer;
import scenes.lib.AssetConstants;
import scenes.lib.settings.UserSettings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class LobbyController extends Controller {

    public static final String HOST_ON_PORT = "hostOnPort";
    public static final String HOST_ADDRESS = "hostAddress";

    private WindowProvider windowProvider;
    private ControllerSwitcher switcher;
    private Settings settings;

    private ArrayList<String> playerNames = new ArrayList<>();
    private LobbyRenderer lobbyRenderer;

    @Override
    public void init(EngineContext context, Parameters parameters) {
        switcher = context.getService(ControllerSwitcher.class);
        windowProvider = context.getService(WindowProvider.class);
        settings = context.getService(Settings.class);

        var assetManager = context.<AssetManager>getService(AssetManager.class);
        var cursor = assetManager.<Cursor>getAsset(AssetConstants.CURSOR);

        lobbyRenderer = new LobbyRenderer();
        lobbyRenderer.setCursor(cursor);
        windowProvider.addComponent(lobbyRenderer);

        var hostOnPort = parameters.getString(LobbyController.HOST_ON_PORT);
        var hostAddress = parameters.getString(LobbyController.HOST_ADDRESS);

        if(hostOnPort != null) {
            playerNames.add(settings.get(UserSettings.class).Username() + " [Host]");
            lobbyRenderer.UpdatePlayerList(playerNames);

            var server = initServer(Integer.parseInt(hostOnPort));
        } else if(hostAddress != null) {
            var client = initClient(hostAddress);
        }

    }

    private IoServer initServer(int port) {
        var server = new IoServer();
        AtomicInteger i = new AtomicInteger(1);
        server.bindConnect(socket -> {
            playerNames.add("player" + i.getAndIncrement());
            lobbyRenderer.UpdatePlayerList(playerNames);
        });

        try {
            server.StartListening(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return server;
    }

    private IoClient initClient(String hostAddress) {
        var client = new IoClient();
        try {

            var addressParts = hostAddress.split(":");
            client.connect(
                    addressParts[0],
                    Integer.parseInt(addressParts[1])
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return client;
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
