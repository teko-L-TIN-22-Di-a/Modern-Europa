package scenes.lib.components;

public record PlayerResources(int playerId, int minerals) {
    public PlayerResources addResources(int addedMinerals) {
        return new PlayerResources(playerId, minerals + addedMinerals);
    }

    public PlayerResources useResources(int usedMinerals) {
        return addResources(usedMinerals*-1);
    }
}
