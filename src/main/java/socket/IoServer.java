package socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class IoServer {
    protected static final Logger logger = LogManager.getLogger(IoServer.class);

    private final Map<String, IoSocket> clients = new ConcurrentHashMap<>();
    private final Thread connectionThread;
    private final Queue<Thread> listenThreads = new ConcurrentLinkedQueue<>();
    private ServerSocket serverSocket;

    private PublishSubject<String> connect = PublishSubject.create();
    private PublishSubject<String> disconnect = PublishSubject.create();

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
                var socket = new IoSocket(serverSocket.accept());
                var uuid = UUID.randomUUID().toString();

                clients.put(uuid, socket);
                logger.info("New client connected <{}>", uuid);
                logger.debug("{} -> {}", uuid, socket.getInetAddress());

                connect.onNext(uuid);

                var listenerThread = createSocketListeningThread(uuid, socket);
                listenThreads.add(listenerThread);
                listenerThread.start();

            } catch (IOException e) {
                logger.error("Exception thrown while listening for new connections: {}", e.getMessage());
            }
        }
    }

    private Thread createSocketListeningThread(String uuid, IoSocket socket){
        return new Thread(() -> {

            try{
                while(socket.isConnected()){
                    var data = socket.read();
                }
            }catch(Exception ex){
                logger.error("Connection to Client <{}> abruptly lost.", uuid);
            }

            clients.remove(uuid);
            disconnect.onNext(uuid);

        });
    }

}
