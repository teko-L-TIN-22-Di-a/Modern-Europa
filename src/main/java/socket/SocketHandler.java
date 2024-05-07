package socket;

import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketHandler implements Closeable {

    private final Socket socket;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;

    private PublishSubject<Void> connect = PublishSubject.create();
    private PublishSubject<Void> disconnect = PublishSubject.create();
    private PublishSubject<String> receive = PublishSubject.create();
    private PublishSubject<String> send = PublishSubject.create();

    public SocketHandler(Socket socket) throws IOException {
        this.socket = socket;
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public Subscription BindConnection(Action1) {
        return connect.subscribe()
    }

    public void Connect() {
        connect.onNext(null);
    }

    public String read() throws IOException {
        var data = inputStream.readUTF();
        receive.onNext(data);
        return data;
    }

    public void Disconnect() {
        disconnect.onNext(null);
    }

    public void send(String data) throws IOException {
        outputStream.writeUTF(data);
        send.onNext(data);
    }

    public String getInetAddress() throws IOException {
        return socket.getInetAddress().toString();
    }

    public boolean isConnected(){
        return socket.isConnected();
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

}
