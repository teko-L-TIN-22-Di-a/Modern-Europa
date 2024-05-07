package socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class IoClient {
    protected static final Logger logger = LogManager.getLogger(IoClient.class);

    private Thread listenThread;
    private IoSocket socket;

    public IoClient() {
        listenThread = new Thread(this::listen);
    }

    public void connect(String address) throws IOException {
        var socket = new Socket();

        // TODO make configurable
        socket.connect(new InetSocketAddress(address, 3000));

        this.socket = new IoSocket(socket);

        logger.info("Connected to <{}>", address);

        listenThread.start();
    }

    public void disconnect() throws IOException {
        socket.close();
        listenThread.interrupt();
    }

    private void listen() {
        try {
            while (socket.isConnected()) {
                var data = socket.read();
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

}
