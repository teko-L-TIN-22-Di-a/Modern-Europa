package core.loading;

import core.Parameters;

public record LoadConfiguration(AssetType type, Parameters parameters) {

    public LoadConfiguration(AssetType type, Parameters parameters) {
        this.type = type;
        this.parameters = parameters;
    }

    public LoadConfiguration(AssetType type) {
        this(type, Parameters.Empty);
    }

}
