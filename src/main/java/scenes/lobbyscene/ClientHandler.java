package scenes.lobbyscene;

import com.google.gson.Gson;
import core.ecs.EcsSnapshot;
import core.networking.IoClient;
import core.util.JsonConverter;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import scenes.lib.PlayerInfo;
import scenes.lib.networking.messages.InitGameMessage;
import scenes.lib.networking.messages.LobbyUpdateMessage;
import scenes.lib.networking.messages.RegisterMessage;
import scenes.lib.networking.messages.SocketMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * LobbyScene internal class only meant to be used for the lobby controller.
 */
class ClientHandler {

    private static final Gson gson = JsonConverter.getInstance();

    private final List<Subscription> subscriptions = new ArrayList<>();
    private IoClient client;
    private final String hostAddress;

    private final PublishSubject<Void> connection = PublishSubject.create();
    private final PublishSubject<List<PlayerInfo>> playerListUpdate = PublishSubject.create();
    private final PublishSubject<EcsSnapshot> gameInit = PublishSubject.create();
    private final PublishSubject<Void> disconnection = PublishSubject.create();

    public ClientHandler(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public IoClient getClient() { return client; }

    public void init() throws IOException {
        client = new IoClient();
        subscriptions.addAll(Arrays.asList(
                client.bindConnect(x -> connection.onNext(null)),
                client.bindReceive(msg -> {
                    var message = gson.fromJson(msg, SocketMessage.class);

                    if(message.type().equals(LobbyUpdateMessage.TYPE)) {
                        var updateMessage = message.getMessage(LobbyUpdateMessage.class);
                        playerListUpdate.onNext(updateMessage.players());
                    }

                    if(message.type().equals(InitGameMessage.TYPE)) {
                        var initGameMessage = message.getMessage(InitGameMessage.class);
                        gameInit.onNext(initGameMessage.ecsSnapshot());
                    }
                }),
                client.bindDisconnect(x -> disconnection.onNext(null))
        ));


        var addressParts = hostAddress.split(":");
        client.connect(
                addressParts[0],
                Integer.parseInt(addressParts[1])
        );
    }

    public void dispose() {
        subscriptions.forEach(Subscription::unsubscribe);
    }

    public void stop() throws IOException {
        client.disconnect();
    }

    public void registerUser(String username) {
        var registerMessage = SocketMessage.of(new RegisterMessage(username));
        client.send(gson.toJson(registerMessage));
    }

    public Subscription bindConnection(Action1<Void> action) {
        return connection.subscribe(action);
    }
    public Subscription bindGameInit(Action1<EcsSnapshot> action) {
        return gameInit.subscribe(action);
    }
    public Subscription bindPlayerListUpdate(Action1<List<PlayerInfo>> action) {
        return playerListUpdate.subscribe(action);
    }
    public Subscription bindDisconnection(Action1<Void> action) {
        return disconnection.subscribe(action);
    }

}
