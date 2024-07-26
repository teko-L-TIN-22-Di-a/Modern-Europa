package core.networking;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class IoServer {
    protected static final Logger logger = LogManager.getLogger(IoServer.class);

    private final List<Subscription> subscriptions = new ArrayList<>();

    private final PublishSubject<IoSocket> connect = PublishSubject.create();
    private final PublishSubject<IoSocket> disconnect = PublishSubject.create();
    private final PublishSubject<IoServerEventData> receive = PublishSubject.create();
    private final PublishSubject<IoServerEventData> send = PublishSubject.create();
    private final Map<String, IoSocket> clients = new ConcurrentHashMap<>();
    private final Queue<Thread> listenThreads = new ConcurrentLinkedQueue<>();
    private final Thread connectionThread;

    private ServerSocket serverSocket;

    public IoServer() {
        connectionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                listenForConnections();
            }
        });
    }

    public Map<String, IoSocket> getClients() { return clients; }

    public void StartListening(int port) throws IOException {
        serverSocket = new ServerSocket(port);
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
                var socket = new IoSocket(handler, uuid);

                logger.info("New client connected <{}>", uuid);
                logger.debug("{} -> {}", uuid, handler.getInetAddress());
                handler.send(uuid);

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
                socket.bindConnect(x -> connect.onNext(socket)),
                socket.bindDisconnect(x -> disconnect.onNext(socket)),
                socket.bindReceive(data -> receive.onNext(new IoServerEventData(socket, data))),
                socket.bindSend(data -> send.onNext(new IoServerEventData(socket, data)))
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

    public void send(String data) throws IOException {
        for(var socket : clients.values()){
            socket.send(data);
        }
    }

    public Subscription bindConnect(Action1<IoSocket> action) {
        return connect.subscribe(action);
    }

    public Subscription bindDisconnect(Action1<IoSocket> action) {
        return disconnect.subscribe(action);
    }

    public Subscription bindReceive(Action1<IoServerEventData> action) {
        return receive.subscribe(action);
    }

    public Subscription bindSend(Action1<IoServerEventData> action) {
        return send.subscribe(action);
    }

}
