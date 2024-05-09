package core.loading;

import core.Parameters;

public record LoadConfiguration(AssetType type, Parameters test) {

    public LoadConfiguration(AssetType type, Parameters test) {
        this.type = type;
        this.test = test;
    }

    public LoadConfiguration(AssetType type) {
        this(type, Parameters.Empty);
    }

}
