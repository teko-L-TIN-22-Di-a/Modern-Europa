package examples;

import core.networking.IoClient;

import java.io.IOException;

public class ClientTest {
    public static void main(String[] args) {
        System.out.println("Start");

        var server = new IoClient();

        server.bindConnect(x -> {
            System.out.println("Connected!");

            server.bindReceive(message -> {
               System.out.println("Received: " + message);

               server.send("Response");

            });
        });

        try {
            server.connect("127.0.0.1");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Done!");
    }

}
