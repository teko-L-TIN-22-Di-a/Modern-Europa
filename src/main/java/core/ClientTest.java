package core;

import socket.IoClient;
import socket.IoServer;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class ClientTest {
    public static void main(String[] args) {
        System.out.println("Start");

        var server = new IoClient();
        try {
            server.connect("127.0.0.1");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Done!");
    }

}
