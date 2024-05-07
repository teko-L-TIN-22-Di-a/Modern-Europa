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

    private List<Subscription> subscriptions = new ArrayList<>();

    private Thread listenThread;
    private IoSocket socket;

    private PublishSubject<Void> connect = PublishSubject.create();
    private PublishSubject<Void> disconnect = PublishSubject.create();
    private PublishSubject<String> receive = PublishSubject.create();
    private PublishSubject<String> send = PublishSubject.create();

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
                socket.BindConnect(x -> connect.onNext(null)),
                socket.BindDisconnect(x -> disconnect.onNext(null)),
                socket.BindReceive(data -> receive.onNext(data)),
                socket.BindSend(data -> send.onNext(data))
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

}
