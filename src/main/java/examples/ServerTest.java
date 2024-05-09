package examples;

import core.networking.IoServer;

import java.io.IOException;

public class ServerTest {
    public static void main(String[] args) {
        System.out.println("Start");

        var server = new IoServer();

        server.bindConnect(socket -> {
           System.out.println("New connection");

           socket.bindReceive(message -> {
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
