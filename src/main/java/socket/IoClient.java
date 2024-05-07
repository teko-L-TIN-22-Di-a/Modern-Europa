package socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IoClient {
    protected static final Logger logger = LogManager.getLogger(IoClient.class);

    private final List<Subscription> subscriptions = new ArrayList<>();

    private final PublishSubject<Void> connect = PublishSubject.create();
    private final PublishSubject<Void> disconnect = PublishSubject.create();
    private final PublishSubject<String> receive = PublishSubject.create();
    private final PublishSubject<String> send = PublishSubject.create();
    private final Thread listenThread;

    private IoSocket socket;

    public IoClient() {
        listenThread = new Thread(this::listen);
    }

    public void connect(String address) throws IOException {
        var socket = new Socket();

        // TODO make configurable
        socket.connect(new InetSocketAddress(address, 3000));

        var handler = new SocketHandler(socket);
        this.socket = new IoSocket(handler);

        bindSocket(this.socket);
        handler.Connect();

        logger.info("Connected to <{}>", address);

        listenThread.start();
    }

    public void disconnect() throws IOException {
        socket.close();
        listenThread.interrupt();
    }

    private void listen() {

        var handler = socket.getHandler();

        try {
            while (handler.isConnected()) {
                var data = handler.read();
                if (data == null) return;
            }
        }catch (Exception ex) {
            logger.error("Connection to Server abruptly lost: {}", ex.getMessage());
        }

        handler.Disconnect();

    }

    public void send(String message) {
        try{
            socket.send(message);
        }catch (Exception ex){
            logger.error("Unable to send message to server: {}", ex.getMessage());
        }
    }

    private void bindSocket(IoSocket socket){
        subscriptions.clear();
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

}
