package core;

import socket.IoServer;

import java.io.IOException;

public class ServerTest {
    public static void main(String[] args) {
        System.out.println("Start");

        var server = new IoServer();
        try {
            server.StartListening();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Done!");
    }
}
