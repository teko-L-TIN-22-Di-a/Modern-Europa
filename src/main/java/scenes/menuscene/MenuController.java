package scenes.menuscene;

import core.Controller;
import core.ControllerSwitcher;
import core.EngineContext;
import core.Parameters;
import core.graphics.WindowProvider;
import core.loading.AssetManager;
import core.loading.Settings;
import scenes.SetupGameController;
import scenes.lib.PlayerInfo;
import scenes.lib.rendering.DialogRenderer;
import scenes.lobbyscene.LobbyController;
import scenes.gamescene.MainController;
import scenes.lib.AssetConstants;
import scenes.lib.settings.UserSettings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class MenuController extends Controller {

    public static final String DISPLAY_ERROR = "displayError";

    private WindowProvider windowProvider;
    private ControllerSwitcher switcher;
    private Settings settings;

    private MenuRenderer menuRenderer;

    @Override
    public void init(EngineContext context, Parameters parameters) {
        switcher = context.getService(ControllerSwitcher.class);
        settings = context.getService(Settings.class);
        windowProvider = context.getService(WindowProvider.class);

        var assetManager = context.<AssetManager>getService(AssetManager.class);
        var cursor = assetManager.<Cursor>getAsset(AssetConstants.CURSOR);

        var errorMessage = parameters.getString(MenuController.DISPLAY_ERROR);

        menuRenderer = new MenuRenderer();
        menuRenderer.setCursor(cursor);
        menuRenderer.bindHostButtonClick(x -> onHostButtonClick());
        menuRenderer.bindJoinButtonClick(x -> onJoinButtonClick());
        menuRenderer.bindFreeModeButtonClick(x -> onFreeModeButtonClick());
        windowProvider.addComponent(menuRenderer);

        if(errorMessage != null) {
            new DialogRenderer(
                    errorMessage,
                    JOptionPane.ERROR_MESSAGE,
                    JOptionPane.OK_OPTION
            ).showDialog(menuRenderer.getParent(), "Error");
        }

        var userSettings = settings.get(UserSettings.class);
        if(userSettings == null) {
            requestName();
        }

    }

    private void onHostButtonClick() {
        switcher.queue(new LobbyController(), new Parameters(Map.ofEntries(
                entry(LobbyController.LOBBY_CONTROLLER_TYPE, LobbyController.HOST_LOBBY),
                entry(LobbyController.HOST_ON_PORT, "3000")
        )));
    }

    private void requestName() {
        var textInput = new JTextField();
        textInput.setText("Player");

        new DialogRenderer(
                Arrays.asList("Please enter a username.", textInput).toArray(),
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_OPTION
        ).showDialog(menuRenderer.getParent(), "Username");

        var username = textInput.getText();
        if(username.isBlank() || username.isEmpty()) {
            username = "Player";
        }

        settings.put(UserSettings.class.getSimpleName(), new UserSettings(username));
        settings.save();
    }

    private void onJoinButtonClick() {
        var textInput = new JTextField();
        textInput.setText("localhost:3000");

        var result = new DialogRenderer(
                Arrays.asList("Please enter the Server Host address.", textInput).toArray(),
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION
        ).showDialog(menuRenderer.getParent(), "Join");

        if (result == JOptionPane.OK_OPTION) {
            switcher.queue(new LobbyController(), new Parameters(Map.ofEntries(
                    entry(LobbyController.LOBBY_CONTROLLER_TYPE, LobbyController.CLIENT_LOBBY),
                    entry(LobbyController.HOST_ADDRESS, textInput.getText())
            )));
        }
    }

    private void onFreeModeButtonClick() {
        switcher.queue(new SetupGameController(), new Parameters(Map.ofEntries(
            entry(SetupGameController.PLAYERS, List.of(
                    new PlayerInfo("FreeModePlayer", false, "", 1)
            ))
        )));
    }

    @Override
    public void update(double delta) {
        // Do nothing
    }

    @Override
    public void cleanup() {
        // Do nothing
    }
}
