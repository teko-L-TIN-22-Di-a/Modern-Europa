package scenes.gamescene;

import com.google.gson.Gson;
import core.ecs.EcsView;
import core.networking.IoServer;
import core.util.JsonConverter;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import scenes.lib.PlayerInfo;
import scenes.lib.components.Command;
import scenes.lib.networking.messages.CommandMessage;
import scenes.lib.networking.messages.SocketMessage;

import java.io.IOException;
import java.util.*;

public class ServerHandler {

    private static final Gson gson = JsonConverter.getInstance();

    private final List<Subscription> subscriptions = new ArrayList<>();
    private IoServer server;
    private List<PlayerInfo> playerInfos;

    private final PublishSubject<PlayerInfo> playerDisconnection = PublishSubject.create();
    private final PublishSubject<CommandMessage> receivedCommands = PublishSubject.create();

    public ServerHandler(IoServer server, List<PlayerInfo> playerInfos) {
        this.server = server;
    }

    public void init() {
        server.getClients().values().forEach(socket -> {
            subscriptions.addAll(Arrays.asList(
                    server.bindDisconnect(x -> {
                        var playerInfo = playerInfos.stream().filter(info -> info.socketId() == socket.getUuid()).findFirst();

                        playerInfo.ifPresent(playerDisconnection::onNext);
                    }),
                    socket.bindReceive(msg -> {
                        var message = gson.fromJson(msg, SocketMessage.class);

                        if(message.type().equals(CommandMessage.TYPE)) {
                            var commandMessage = message.getMessage(CommandMessage.class);
                            receivedCommands.onNext(commandMessage);
                        }
                    })
            ));
        });
    }

    public void dispose() {
        subscriptions.forEach(Subscription::unsubscribe);
    }

    public void sendCommandList(List<EcsView<Command>> commands) {
        var updateMessage = SocketMessage.of(new CommandMessage(commands));
        try {
            server.send(gson.toJson(updateMessage));
        } catch (IOException e) {
            // TODO Handle this
            throw new RuntimeException(e);
        }
    }

    public Subscription bindReceivedCommands(Action1<CommandMessage> action) {
        return receivedCommands.subscribe(action);
    }
    public Subscription bindPlayerDisconnection(Action1<PlayerInfo> action) {
        return playerDisconnection.subscribe(action);
    }

}
