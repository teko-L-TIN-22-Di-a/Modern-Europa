package scenes.gamescene;

import com.google.gson.Gson;
import core.networking.IoClient;
import core.util.JsonConverter;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import scenes.lib.networking.messages.InitGameMessage;
import scenes.lib.networking.messages.LobbyUpdateMessage;
import scenes.lib.networking.messages.SocketMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class ClientHandler {

    private static final Gson gson = JsonConverter.getInstance();

    private final List<Subscription> subscriptions = new ArrayList<>();
    private IoClient client;

    private final PublishSubject<Void> disconnection = PublishSubject.create();

    public ClientHandler(IoClient client) {
        this.client = client;
    }

    public void init() {
        subscriptions.addAll(Arrays.asList(
                client.bindDisconnect(x -> disconnection.onNext(null))
        ));
    }

    public void dispose() {
        subscriptions.forEach(Subscription::unsubscribe);
    }

    public Subscription bindDisconnection(Action1<Void> action) {
        return disconnection.subscribe(action);
    }

}
