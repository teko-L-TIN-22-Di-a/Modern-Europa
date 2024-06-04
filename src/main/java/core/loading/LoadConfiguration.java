package core.loading;

import core.Parameters;

public record LoadConfiguration(AssetType type, Parameters parameters) {

    public static LoadConfiguration DefaultImage = new LoadConfiguration(AssetType.Image);

    public LoadConfiguration(AssetType type, Parameters parameters) {
        this.type = type;
        this.parameters = parameters;
    }

    public LoadConfiguration(AssetType type) {
        this(type, Parameters.EMPTY);
    }

}
