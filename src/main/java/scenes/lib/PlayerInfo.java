package scenes.lib;

public record PlayerInfo(String name, boolean isHost, String socketId, int id) {
    public static PlayerInfo forUser(String name, String socketId) {
        return new PlayerInfo(name, false, socketId, -1);
    }
    public static PlayerInfo forHost(String name) {
        return new PlayerInfo(name, true, "", -1);
    }
    public PlayerInfo withId(int id) {
        return new PlayerInfo(name, isHost, socketId, id);
    }
}
