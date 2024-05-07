package socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class IoServer {
    protected static final Logger logger = LogManager.getLogger(IoServer.class);

    private List<Subscription> subscriptions = new ArrayList<>();

    private final Map<String, IoSocket> clients = new ConcurrentHashMap<>();
    private final Thread connectionThread;
    private final Queue<Thread> listenThreads = new ConcurrentLinkedQueue<>();
    private ServerSocket serverSocket;

    private PublishSubject<IoSocket> connect = PublishSubject.create();
    private PublishSubject<IoSocket> disconnect = PublishSubject.create();
    private PublishSubject<IoServerEventData> receive = PublishSubject.create();
    private PublishSubject<IoServerEventData> send = PublishSubject.create();

    public IoServer() {
        connectionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                listenForConnections();
            }
        });
    }

    public void StartListening() throws IOException {
        // TODO make configurable
        serverSocket = new ServerSocket(3000);
        connectionThread.start();
    }

    public void StopListening() throws IOException {
        serverSocket.close();
        listenThreads.forEach(Thread::interrupt);
        listenThreads.clear();
    }

    private void listenForConnections() {
        logger.info("Now listening on <{}>", serverSocket.getLocalPort());

        while(!serverSocket.isClosed()) {
            try {
                var handler = new SocketHandler(serverSocket.accept());
                var uuid = UUID.randomUUID().toString();
                var socket = new IoSocket(handler);

                logger.info("New client connected <{}>", uuid);
                logger.debug("{} -> {}", uuid, handler.getInetAddress());

                bindSocket(socket);
                clients.put(uuid, socket);

                var listenerThread = createSocketListeningThread(uuid, socket);
                listenThreads.add(listenerThread);
                listenerThread.start();

            } catch (IOException e) {
                logger.error("Exception thrown while listening for new connections: {}", e.getMessage());
            }
        }
    }

    private void bindSocket(IoSocket socket){
        subscriptions.addAll(Arrays.asList(
                socket.BindConnect(x -> connect.onNext(socket)),
                socket.BindDisconnect(x -> disconnect.onNext(socket)),
                socket.BindReceive(data -> receive.onNext(new IoServerEventData(socket, data))),
                socket.BindSend(data -> send.onNext(new IoServerEventData(socket, data)))
        ));
    }

    private Thread createSocketListeningThread(String uuid, IoSocket socket){
        return new Thread(() -> {

            var handler = socket.getHandler();

            handler.Connect();

            try{
                while(handler.isConnected()){
                    handler.read();
                }
            }catch(Exception ex){
                logger.error("Connection to Client <{}> abruptly lost.", uuid);
            }

            clients.remove(uuid);
            handler.Disconnect();

        });
    }

    public Subscription BindConnect(Action1<IoSocket> action) {
        return connect.subscribe(action);
    }

    public Subscription BindDisconnect(Action1<IoSocket> action) {
        return disconnect.subscribe(action);
    }

    public Subscription BindReceive(Action1<IoServerEventData> action) {
        return receive.subscribe(action);
    }

    public Subscription BindSend(Action1<IoServerEventData> action) {
        return send.subscribe(action);
    }

}
