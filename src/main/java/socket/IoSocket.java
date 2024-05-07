package socket;

import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IoSocket implements Closeable {

    private SocketHandler socket;

    private List<Subscription> subscriptions = new ArrayList<>();

    private PublishSubject<Void> connect = PublishSubject.create();
    private PublishSubject<Void> disconnect = PublishSubject.create();
    private PublishSubject<String> receive = PublishSubject.create();
    private PublishSubject<String> send = PublishSubject.create();

    public IoSocket(SocketHandler socketHandler) {
        socket = socketHandler;

        subscriptions.addAll(Arrays.asList(
                socket.BindConnect(x -> connect.onNext(null)),
                socket.BindDisconnect(x -> disconnect.onNext(null)),
                socket.BindReceive(x -> receive.onNext(x)),
                socket.BindSend(x -> send.onNext(x))
        ));
    }

    public Subscription BindConnect(Action1<Void> action) {
        return connect.subscribe(action);
    }

    public Subscription BindDisconnect(Action1<Void> action) {
        return disconnect.subscribe(action);
    }

    public Subscription BindReceive(Action1<String> action) {
        return receive.subscribe(action);
    }

    public Subscription BindSend(Action1<String> action) {
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
