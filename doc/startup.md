# Startup

The building of an Engine is kept similar to how the Startup of a C# asp net core application is done.
Following is a minimal example of how a Main method could look using the EngineBuilder.

```java
new Engine.Builder()
    .bootstrapController(new MainController())
    .setFramerate(60)
    .configureServices(builder -> {
        JFrameWindowProvider.addToServices(builder);
    })
    .startupServices(context -> {
        JFrameWindowProvider.initWindow(context, window -> {
            window.setTitle(WindowConfig.Title);
        });
    })
    .build()
    .run();
```

Using the "bootstrapController" method a starting Controller can be defined.
"configureServices" is used to add the services to the context.
"startupServices" is used to initialise added services which will happen after them being added and the
EngineContext being created.

In above example you can also see that the Service "JFrameWindowProvider" is setup.
This will automatically setup and handle a Window that can be later accessed through the "WindowProvider" interface.

Additionally an example of a Controller can be found in the "controller.md" file.
