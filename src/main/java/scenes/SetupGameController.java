package scenes;

import com.google.gson.Gson;
import core.Controller;
import core.ControllerSwitcher;
import core.EngineContext;
import core.Parameters;
import core.ecs.Ecs;
import core.ecs.EcsSnapshot;
import core.ecs.Entity;
import core.ecs.components.Position;
import core.networking.IoServer;
import core.networking.IoSocket;
import core.util.JsonConverter;
import core.util.Vector2f;
import core.util.Vector3f;
import main.Main;
import scenes.gamescene.MainController;
import scenes.lib.MapInfo;
import scenes.lib.PlayerInfo;
import scenes.lib.components.TerrainChunk;
import scenes.lib.entities.EntityHelper;
import scenes.lib.networking.InitGameMessage;
import scenes.lib.networking.LobbyUpdateMessage;
import scenes.lib.networking.SocketMessage;
import scenes.lobbyscene.LobbyController;

import java.io.IOException;
import java.util.*;

import static java.util.Map.entry;

public class SetupGameController extends Controller {

    private static final Gson gson = JsonConverter.getInstance();

    public static final String SERVER = "server";
    public static final String PLAYERS = "players";

    private Ecs ecs;
    private ControllerSwitcher switcher;
    private IoServer server;
    private EcsSnapshot ecsSnapshot;

    @Override
    public void init(EngineContext context, Parameters parameters) {
        switcher = context.getService(ControllerSwitcher.class);
        ecs = context.getService(Ecs.class);

        server = parameters.get(SERVER);
        var players = parameters.<List<PlayerInfo>>get(PLAYERS);

        // TODO properly configure map
        var mapInfo = new MapInfo(Arrays.asList(
                Vector2f.of(1, 1),
                Vector2f.of(23, 23),
                Vector2f.of(23, 1),
                Vector2f.of(1, 23)
        ));

        var playerSlot = new LinkedList<Vector2f>(mapInfo.startPoints());
        var playerId = 1;
        var newPlayerList = new ArrayList<PlayerInfo>();
        for(var player : players) {
            var slot = playerSlot.remove();
            if(slot == null) {
                throw new RuntimeException("Too many players for map");
            }

            var mainBase = EntityHelper.createMainBase(ecs, playerId);
            mainBase.setComponent(new Position(slot.toVector3fy(0)));

            var generator = EntityHelper.createGenerator(ecs, playerId);
            generator.setComponent(new Position(slot.toVector3fy(0).add(1, 0, 0)));

            var mainUnit = EntityHelper.createUnit(ecs, playerId);
            mainUnit.setComponent(new Position(slot.toVector3fy(0).add(0, 0, 1)));

            newPlayerList.add(player.withId(playerId));
            playerId++;
        }

        ecsSnapshot = ecs.getSnapshot();

        var playerUpdateMessage = SocketMessage.of(new LobbyUpdateMessage(newPlayerList));
        try {
            server.send(gson.toJson(playerUpdateMessage));
        } catch (IOException e) {
            // TODO Handle this
            throw new RuntimeException(e);
        }

        var gameInitMessage = SocketMessage.of(new InitGameMessage(ecsSnapshot));
        try {
            server.send(gson.toJson(gameInitMessage));
        } catch (IOException e) {
            // TODO Handle this
            throw new RuntimeException(e);
        }

    }

    @Override
    public void update() {
        switcher.switchTo(new MainController(), new Parameters(Map.ofEntries(
                entry(MainController.PLAYER_ID, 1),
                entry(MainController.ECS_SNAPSHOT, ecsSnapshot),
                entry(MainController.HOSTING_SOCKET, server)
        )));
    }

    @Override
    public void cleanup() {

    }
}
