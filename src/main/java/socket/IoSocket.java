package socket;

import rx.subjects.PublishSubject;

import java.io.*;
import java.net.Socket;

public class IoSocket implements Closeable {

    private SocketHandler socket;

    private PublishSubject<String> connect = PublishSubject.create();
    private PublishSubject<String> disconnect = PublishSubject.create();
    private PublishSubject<String> receive = PublishSubject.create();

    public IoSocket(SocketHandler socketHandler) {
        socket = socketHandler;
    }

    public SocketHandler getSocket() {
        return socket;
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
