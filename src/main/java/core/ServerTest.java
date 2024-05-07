package core;

import socket.IoServer;

import java.io.IOException;

public class ServerTest {
    public static void main(String[] args) {
        System.out.println("Start");

        var server = new IoServer();

        server.BindConnect(socket -> {
           System.out.println("New connection");

           socket.BindReceive(message -> {
              System.out.println("Received: " + message);
           });

            try {
                socket.send("Hey");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });

        try {
            server.StartListening();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Done!");
    }
}
