package scenes.lobbyscene;

import com.google.gson.Gson;
import core.networking.IoClient;
import core.networking.IoServer;
import core.networking.IoSocket;
import core.util.JsonConverter;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import scenes.lib.PlayerInfo;
import scenes.lib.networking.LobbyUpdateMessage;
import scenes.lib.networking.RegisterMessage;
import scenes.lib.networking.SocketMessage;

import java.io.IOException;
import java.util.*;

/**
 * LobbyScene internal class only meant to be used for the lobby controller.
 */
class ServerHandler {

    private static final Gson gson = JsonConverter.getInstance();

    private final List<Subscription> subscriptions = new ArrayList<>();
    private IoServer server;
    private final int port;

    private final Map<IoSocket, PlayerInfo> usernameMap = new HashMap<>();

    private final PublishSubject<PlayerInfo> playerConnection = PublishSubject.create();
    private final PublishSubject<PlayerInfo> playerDisconnection = PublishSubject.create();

    public ServerHandler(int port) {
        this.port = port;
    }

    public IoServer getServer() { return server; }

    public void init() {
        server = new IoServer();
        subscriptions.add(
                server.bindConnect(this::onConnect)
        );

        try {
            server.StartListening(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void dispose() {
        subscriptions.forEach(Subscription::unsubscribe);
    }

    public Subscription bindPlayerConnection(Action1<PlayerInfo> action) {
        return playerConnection.subscribe(action);
    }
    public Subscription bindPlayerDisconnection(Action1<PlayerInfo> action) {
        return playerDisconnection.subscribe(action);
    }

    private void onConnect(IoSocket socket) {
        subscriptions.addAll(Arrays.asList(
                socket.bindDisconnect(x -> {
                    if(!usernameMap.containsKey(socket)) {
                        return;
                    }
                    playerDisconnection.onNext(usernameMap.get(socket));
                }),
                socket.bindReceive(msg -> {
                    var message = gson.fromJson(msg, SocketMessage.class);
                    if(message.type().equals(RegisterMessage.TYPE)) {
                        var registerMessage = message.getMessage(RegisterMessage.class);

                        var playerInfo = PlayerInfo.forUser(registerMessage.username(), socket.getUuid());
                        usernameMap.put(socket, playerInfo);
                        playerConnection.onNext(playerInfo);
                    }
                })
        ));
    }

    public void updatePlayerList(List<PlayerInfo> players) {
        var updateMessage = SocketMessage.of(new LobbyUpdateMessage(players));
        try {
            server.send(gson.toJson(updateMessage));
        } catch (IOException e) {
            // TODO Handle this
            throw new RuntimeException(e);
        }
    }

}
