package main;


import controllers.TestController;
import core.Engine;
import core.graphics.FlatLightLafExtension;
import core.graphics.JFrameWindowProvider;
import core.loading.DefaultAssetManager;
import core.loading.FileAssetLoader;
import core.loading.JsonSettings;

public class Main {
    public static void main(String[] args) {

        try {
            new Engine.Builder()
                    .bootstrapController(new TestController())
                    .configureServices(builder -> {

                        DefaultAssetManager.addToServices(builder);
                        FileAssetLoader.addToServices(builder);
                        JsonSettings.addToServices(builder, "settings.json");
                        JFrameWindowProvider.addToServices(builder);

                    })
                    .startupServices(context -> {

                        FlatLightLafExtension.init();
                        FileAssetLoader.init(context);
                        JFrameWindowProvider.initWindow(context);

                    })
                    .build()
                    .run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}