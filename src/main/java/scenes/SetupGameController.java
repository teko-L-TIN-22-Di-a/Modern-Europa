package scenes;

import com.google.gson.Gson;
import core.Controller;
import core.ControllerSwitcher;
import core.EngineContext;
import core.Parameters;
import core.ecs.Ecs;
import core.ecs.EcsSnapshot;
import core.ecs.components.Position;
import core.networking.IoServer;
import core.util.JsonConverter;
import core.util.Vector2f;
import scenes.gamescene.MainController;
import scenes.lib.MapInfo;
import scenes.lib.PlayerInfo;
import scenes.lib.entities.EntityHelper;
import scenes.lib.networking.messages.InitGameMessage;
import scenes.lib.networking.messages.LobbyUpdateMessage;
import scenes.lib.networking.messages.SocketMessage;

import java.io.IOException;
import java.util.*;

import static java.util.Map.entry;

public class SetupGameController extends Controller {

    private static final Gson gson = JsonConverter.getInstance();

    public static final String SERVER_SOCKET = "server";
    public static final String PLAYERS = "players";

    private Ecs ecs;
    private ControllerSwitcher switcher;
    private IoServer server = null;
    private EcsSnapshot ecsSnapshot;
    private List<PlayerInfo> newPlayerList;

    @Override
    public void init(EngineContext context, Parameters parameters) {
        switcher = context.getService(ControllerSwitcher.class);
        ecs = context.getService(Ecs.class);

        server = parameters.get(SERVER_SOCKET);
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
        newPlayerList = new ArrayList<PlayerInfo>();
        for(var player : players) {
            var slot = playerSlot.remove();
            if(slot == null) {
                throw new RuntimeException("Too many players for map");
            }

            var mainBase = EntityHelper.createMainBase(ecs, playerId);
            mainBase.setComponent(new Position(slot.add(0.5f, 0.5f).toVector3fy(0)));

            var miner = EntityHelper.createMiner(ecs, playerId);
            miner.setComponent(new Position(slot.toVector3fy(0).add(1.5f, 0, -0.5f)));

            var generator = EntityHelper.createGenerator(ecs, playerId);
            generator.setComponent(new Position(slot.toVector3fy(0).add(1.5f, 0, 0.5f)));

            for(var i = 0; i < 1; i++) {
                var mainUnit = EntityHelper.createSmallUnit(ecs, playerId);
                mainUnit.setComponent(new Position(slot.toVector3fy(0).add(0.5f, 0, 1.5f)));
            }

            newPlayerList.add(player.withId(playerId));
            playerId++;
        }

        ecsSnapshot = ecs.getSnapshot();

        if(server == null) {
            // FreeMode hack
            switcher.switchTo(new MainController(), new Parameters(Map.ofEntries(
                    entry(MainController.PLAYER_ID, 1),
                    entry(MainController.ECS_SNAPSHOT, ecsSnapshot),
                    entry(MainController.PLAYERS, newPlayerList)
            )));
            return;
        }

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
    public void update(double delta) {
        switcher.switchTo(new MainController(), new Parameters(Map.ofEntries(
                entry(MainController.PLAYER_ID, 1),
                entry(MainController.ECS_SNAPSHOT, ecsSnapshot),
                entry(MainController.SERVER_SOCKET, server),
                entry(MainController.PLAYERS, newPlayerList)
        )));
    }

    @Override
    public void cleanup() {

    }
}
