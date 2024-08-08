package scenes.lobbyscene;

import com.google.gson.Gson;
import core.Controller;
import core.ControllerSwitcher;
import core.EngineContext;
import core.Parameters;
import core.graphics.WindowProvider;
import core.loading.AssetManager;
import core.loading.Settings;
import core.util.JsonConverter;
import scenes.SetupGameController;
import scenes.gamescene.MainController;
import scenes.lib.AssetConstants;
import scenes.lib.PlayerInfo;
import scenes.lib.settings.UserSettings;
import scenes.menuscene.MenuController;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import static java.util.Map.entry;

public class LobbyController extends Controller {

    public static final String HOST_LOBBY = "hostLobby";
    public static final String CLIENT_LOBBY = "clientLobby";
    public static final String LOBBY_CONTROLLER_TYPE = "lobbyControllerType";
    public static final String HOST_ON_PORT = "hostOnPort";
    public static final String HOST_ADDRESS = "hostAddress";

    private WindowProvider windowProvider;
    private ControllerSwitcher switcher;
    private Settings settings;

    private String myUsername;
    private ArrayList<PlayerInfo> players = new ArrayList<>();
    private LobbyRenderer lobbyRenderer;
    private ServerHandler server = null;
    private ClientHandler client = null;
    private String controllerType;

    @Override
    public void init(EngineContext context, Parameters parameters) {
        switcher = context.getService(ControllerSwitcher.class);
        windowProvider = context.getService(WindowProvider.class);
        settings = context.getService(Settings.class);

        var assetManager = context.<AssetManager>getService(AssetManager.class);
        var cursor = assetManager.<Cursor>getAsset(AssetConstants.CURSOR);

        var hostOnPort = parameters.getString(LobbyController.HOST_ON_PORT);
        var hostAddress = parameters.getString(LobbyController.HOST_ADDRESS);
        controllerType = parameters.getString(LobbyController.LOBBY_CONTROLLER_TYPE);

        myUsername = settings.get(UserSettings.class).Username();

        lobbyRenderer = new LobbyRenderer(controllerType.equals(HOST_LOBBY));
        lobbyRenderer.setCursor(cursor);
        windowProvider.addComponent(lobbyRenderer);

        lobbyRenderer.bindBackButtonClick(e -> onBackButtonClick());

        switch(controllerType) {
            case HOST_LOBBY:
                lobbyRenderer.bindStartButtonClick(e -> onStartButtonClick());
                players.add(PlayerInfo.forHost(myUsername));
                lobbyRenderer.UpdatePlayerList(players);

                server = initServer(Integer.parseInt(hostOnPort));
                break;
            case CLIENT_LOBBY:
                client = initClient(hostAddress);
                break;
            default:
                throw new RuntimeException("Invalid controllerType: " + controllerType);
        }

    }

    private ServerHandler initServer(int port) {
        var server = new ServerHandler(port);
        server.bindPlayerConnection(playerInfo -> {
            players.add(playerInfo);
            server.updatePlayerList(players);
            lobbyRenderer.UpdatePlayerList(players);
        });
        server.bindPlayerDisconnection(playerInfo -> {
            players.remove(playerInfo);
            server.updatePlayerList(players);
            lobbyRenderer.UpdatePlayerList(players);
        });

        try {
            server.init();
        } catch (IOException e) {
            switcher.switchTo(new MenuController(), new Parameters(Map.ofEntries(
                    entry(MenuController.DISPLAY_ERROR, "Couldn't start server.")
            )));
        }

        return server;
    }

    private ClientHandler initClient(String hostAddress) {
        var client = new ClientHandler(hostAddress);
        client.bindConnection(x -> {
            client.registerUser(myUsername);
        });
        client.bindPlayerListUpdate(playerList -> {
            players = new ArrayList<>(playerList);
            lobbyRenderer.UpdatePlayerList(players);
        });
        client.bindDisconnection(username -> {
            switcher.queue(new MenuController(), new Parameters(Map.ofEntries(
                    entry(MenuController.DISPLAY_ERROR, "Disconnect from host")
            )));
        });
        client.bindGameInit(snapshot -> {

            var player = players.stream()
                    .filter(playerInfo -> playerInfo.socketId().equals(client.getClient().getUuid()))
                    .findFirst();

            if(player.isEmpty()) {
                throw new RuntimeException("Player entry for client not found");
            }

            switcher.queue(new MainController(), new Parameters(Map.ofEntries(
                    entry(MainController.CLIENT_SOCKET, client.getClient()),
                    entry(MainController.ECS_SNAPSHOT, snapshot),
                    entry(MainController.PLAYER_ID, player.get().id())
            )));
        });
        try {
            client.init();
        } catch (IOException e) {
            switcher.switchTo(new MenuController(), new Parameters(Map.ofEntries(
                    entry(MenuController.DISPLAY_ERROR, "Couldn't join server.")
            )));
        }
        return client;
    }

    public void onBackButtonClick() {

        try {
            if(server != null) {
                server.stop();
            }
            if(client != null) {
                client.stop();
            }
        }catch (IOException e) {
            // Do nothing
        }

        switcher.queue(new MenuController());
    }

    public void onStartButtonClick() {

        if(controllerType.equals(CLIENT_LOBBY)) {
            throw new RuntimeException("Start button received as client!");
        }

        switcher.queue(new SetupGameController(), new Parameters(Map.ofEntries(
                entry(SetupGameController.SERVER_SOCKET, server.getServer()),
                entry(SetupGameController.PLAYERS, players)
        )));
    }

    @Override
    public void update() {
        // Do nothing
    }

    @Override
    public void cleanup() {
        if(server != null) {
            server.dispose();
        }
        if(client != null) {
            client.dispose();
        }
    }

}
