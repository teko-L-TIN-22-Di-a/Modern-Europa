package core.networking;

import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IoSocket implements Closeable {

    private final List<Subscription> subscriptions = new ArrayList<>();

    private final PublishSubject<Void> connect = PublishSubject.create();
    private final PublishSubject<Void> disconnect = PublishSubject.create();
    private final PublishSubject<String> receive = PublishSubject.create();
    private final PublishSubject<String> send = PublishSubject.create();

    private SocketHandler socket;

    public IoSocket(SocketHandler socketHandler) {
        socket = socketHandler;

        subscriptions.addAll(Arrays.asList(
                socket.bindConnect(x -> connect.onNext(null)),
                socket.bindDisconnect(x -> disconnect.onNext(null)),
                socket.bindReceive(receive::onNext),
                socket.bindSend(send::onNext)
        ));
    }

    public Subscription bindConnect(Action1<Void> action) {
        return connect.subscribe(action);
    }

    public Subscription bindDisconnect(Action1<Void> action) {
        return disconnect.subscribe(action);
    }

    public Subscription bindReceive(Action1<String> action) {
        return receive.subscribe(action);
    }

    public Subscription bindSend(Action1<String> action) {
        return send.subscribe(action);
    }

    public SocketHandler getHandler() {
        return socket;
    }

    public void send(String message) throws IOException {
        socket.send(message);
    }

    @Override
    public void close() throws IOException {
        socket.close();
        subscriptions.forEach(Subscription::unsubscribe);
    }
}
