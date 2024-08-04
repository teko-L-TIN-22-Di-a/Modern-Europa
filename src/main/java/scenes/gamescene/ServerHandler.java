package scenes.gamescene;

import com.google.gson.Gson;
import core.networking.IoServer;
import core.networking.IoSocket;
import core.util.JsonConverter;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import scenes.lib.PlayerInfo;

import java.io.IOException;
import java.util.*;

class ServerHandler {

    private static final Gson gson = JsonConverter.getInstance();

    private final List<Subscription> subscriptions = new ArrayList<>();
    private IoServer server;
    private List<PlayerInfo> playerInfos;

    private final PublishSubject<PlayerInfo> playerDisconnection = PublishSubject.create();

    public ServerHandler(IoServer server, List<PlayerInfo> playerInfos) {
        this.server = server;
    }

    public void init() {
        server = new IoServer();

        server.getClients().values().forEach(socket -> {
            subscriptions.addAll(Arrays.asList(
                    server.bindConnect(x -> {
                        var playerInfo = playerInfos.stream().filter(info -> info.socketId() == socket.getUuid()).findFirst();

                        playerInfo.ifPresent(playerDisconnection::onNext);
                    })
            ));
        });
    }

    public void dispose() {
        subscriptions.forEach(Subscription::unsubscribe);
    }

    public Subscription bindPlayerDisconnection(Action1<PlayerInfo> action) {
        return playerDisconnection.subscribe(action);
    }

}
