package scenes.gamescene;

import com.google.gson.Gson;
import core.ecs.EcsView;
import core.ecs.EcsView2;
import core.networking.IoClient;
import core.util.JsonConverter;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import scenes.lib.components.Command;
import scenes.lib.components.NetSynch;
import scenes.lib.networking.messages.CommandMessage;
import scenes.lib.networking.messages.InitGameMessage;
import scenes.lib.networking.messages.LobbyUpdateMessage;
import scenes.lib.networking.messages.SocketMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientHandler {

    private static final Gson gson = JsonConverter.getInstance();

    private final List<Subscription> subscriptions = new ArrayList<>();
    private IoClient client;

    private final PublishSubject<Void> disconnection = PublishSubject.create();
    private final PublishSubject<CommandMessage> receivedCommands = PublishSubject.create();

    public ClientHandler(IoClient client) {
        this.client = client;
    }

    public void init() {
        subscriptions.addAll(Arrays.asList(
                client.bindDisconnect(x -> disconnection.onNext(null)),
                client.bindReceive(msg -> {
                    var message = gson.fromJson(msg, SocketMessage.class);

                    if(message.type().equals(CommandMessage.TYPE)) {
                        var commandMessage = message.getMessage(CommandMessage.class);
                        receivedCommands.onNext(commandMessage);
                    }
                })
        ));
    }

    public void dispose() {
        subscriptions.forEach(Subscription::unsubscribe);
    }

    public Subscription bindDisconnection(Action1<Void> action) {
        return disconnection.subscribe(action);
    }
    public Subscription bindReceivedCommands(Action1<CommandMessage> action) {
        return receivedCommands.subscribe(action);
    }

    public void sendCommandList(List<EcsView<Command>> commands) {
        var updateMessage = SocketMessage.of(new CommandMessage(commands));
        client.send(gson.toJson(updateMessage));
    }

}
