package main;


import config.WindowConfig;
import controllers.AnotherTestController;
import controllers.RenderingTestController;
import controllers.TestController;
import core.Engine;
import core.ecs.Ecs;
import core.graphics.FlatLightLafExtension;
import core.graphics.JFrameWindowProvider;
import core.input.JFrameInputBuffer;
import core.input.JFrameMouseListener;
import core.loading.DefaultAssetManager;
import core.loading.FileAssetLoader;
import core.loading.JsonSettings;

public class Main {
    public static void main(String[] args) {

        try {
            new Engine.Builder()
                    //.bootstrapController(new AnotherTestController())
                    .bootstrapController(new RenderingTestController())
                    .setFramerate(60)
                    .configureServices(builder -> {

                        Ecs.addToServices(builder);

                        // Adding Asset and Settings Services
                        DefaultAssetManager.addToServices(builder);
                        FileAssetLoader.addToServices(builder);
                        JsonSettings.addToServices(builder, "settings.json");

                        // Add JFrame Services
                        JFrameWindowProvider.addToServices(builder);
                        JFrameInputBuffer.addToServices(builder);
                        JFrameMouseListener.addToServices(builder);
                    })
                    .startupServices(context -> {

                        Ecs.init(context);

                        FileAssetLoader.init(context);

                        // Init JFrame
                        FlatLightLafExtension.init();
                        JFrameWindowProvider.initWindow(context, window -> {
                            window.setTitle(WindowConfig.Title);
                        });
                        JFrameInputBuffer.init(context);
                        JFrameMouseListener.init(context);
                    })
                    .build()
                    .run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}