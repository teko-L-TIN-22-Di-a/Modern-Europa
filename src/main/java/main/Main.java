package main;


import controllers.TestController;
import core.Engine;

public class Main {
    public static void main(String[] args) {

        try {
            new Engine.Builder()
                    .bootstrapController(new TestController())
                    .configureServices(builder -> {

                    })
                    .startupServices(builder -> {

                    })
                    .build()
                    .run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}