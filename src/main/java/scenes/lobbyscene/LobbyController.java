package scenes.lobbyscene;

import com.google.gson.Gson;
import core.Controller;
import core.ControllerSwitcher;
import core.EngineContext;
import core.Parameters;
import core.graphics.WindowProvider;
import core.loading.AssetManager;
import core.loading.Settings;
import core.networking.IoClient;
import core.networking.IoServer;
import core.util.JsonConverter;
import scenes.gamescene.MainController;
import scenes.lib.AssetConstants;
import scenes.lib.networking.LobbyUpdateMessage;
import scenes.lib.networking.RegisterMessage;
import scenes.lib.networking.SocketMessage;
import scenes.lib.settings.UserSettings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Map.entry;

public class LobbyController extends Controller {

    public static final String HOST_ON_PORT = "hostOnPort";
    public static final String HOST_ADDRESS = "hostAddress";

    private static final Gson gson = JsonConverter.getInstance();

    private WindowProvider windowProvider;
    private ControllerSwitcher switcher;
    private Settings settings;

    private String myUsername;
    private ArrayList<String> playerNames = new ArrayList<>();
    private LobbyRenderer lobbyRenderer;
    private IoServer server = null;
    private IoClient client = null;

    @Override
    public void init(EngineContext context, Parameters parameters) {
        switcher = context.getService(ControllerSwitcher.class);
        windowProvider = context.getService(WindowProvider.class);
        settings = context.getService(Settings.class);

        var assetManager = context.<AssetManager>getService(AssetManager.class);
        var cursor = assetManager.<Cursor>getAsset(AssetConstants.CURSOR);

        var hostOnPort = parameters.getString(LobbyController.HOST_ON_PORT);
        var hostAddress = parameters.getString(LobbyController.HOST_ADDRESS);

        myUsername = settings.get(UserSettings.class).Username();

        var isHost = hostOnPort != null;

        lobbyRenderer = new LobbyRenderer(isHost);
        lobbyRenderer.setCursor(cursor);
        windowProvider.addComponent(lobbyRenderer);

        if(hostOnPort != null) {
            lobbyRenderer.bindStartButtonClick(e -> onStartButtonClick());
            playerNames.add(myUsername + " [Host]");
            lobbyRenderer.UpdatePlayerList(playerNames);

            server = initServer(Integer.parseInt(hostOnPort));
        } else if(hostAddress != null) {
            client = initClient(hostAddress);
        }

    }

    private IoServer initServer(int port) {
        // TODO move code to serverHandler
        var server = new IoServer();
        server.bindConnect(socket -> {
            socket.bindReceive(msg -> {
                System.out.println("Server Message Received! " + msg + RegisterMessage.TYPE);
                var message = gson.fromJson(msg, SocketMessage.class);
                if(message.type().equals(RegisterMessage.TYPE)) {
                    System.out.println("Sending");
                    var registerMessage = message.getMessage(RegisterMessage.class);

                    playerNames.add(registerMessage.username());
                    lobbyRenderer.UpdatePlayerList(playerNames);

                    var updateMessage = SocketMessage.of(new LobbyUpdateMessage(playerNames));
                    try {
                        server.send(gson.toJson(updateMessage));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        });

        try {
            server.StartListening(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return server;
    }

    private IoClient initClient(String hostAddress) {
        // TODO move code to clientHandler
        var client = new IoClient();
        client.bindConnect(x -> {
            System.out.println("Client Connected!");
            var registerMessage = SocketMessage.of(new RegisterMessage(myUsername));

            client.send(gson.toJson(registerMessage));
        });
        client.bindReceive(msg -> {
            System.out.println("Client received update");
            var message = gson.fromJson(msg, SocketMessage.class);
            if(message.type().equals(LobbyUpdateMessage.TYPE)) {
                var updateMessage = message.getMessage(LobbyUpdateMessage.class);
                playerNames = updateMessage.users();
                lobbyRenderer.UpdatePlayerList(playerNames);
            }
        });

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

    public void onStartButtonClick() {

        // TODO handle differently depending on server or client.

        if(server != null) {
            switcher.queue(new MainController(), new Parameters(Map.ofEntries(
                    entry(MainController.PLAYER_ID, 1),
                    entry(MainController.PLAYER_NAME, myUsername),
                    entry(MainController.HOSTING_SOCKET, server)
            )));
        } else if(client != null) {
            switcher.queue(new MainController(), new Parameters(Map.ofEntries(
                    entry(MainController.PLAYER_ID, 1),
                    entry(MainController.PLAYER_NAME, myUsername),
                    entry(MainController.CLIENT_SOCKET, client)
            )));
        } else {
            throw new RuntimeException("Neither the server nor the client was initialized!");
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
