package com.anogaijin.colour.assets;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Created by adunne on 2015/10/19.
 */
public class LoaderDefinition implements Json.Serializable {
    Class<?> type;
    Class<? extends AssetLoader> loader;

    @Override
    public void write(Json json) {
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        try {
            type = Class.forName(json.readValue("type", String.class, jsonData));
            loader = Class.forName(json.readValue("loader", String.class, jsonData)).asSubclass(AssetLoader.class);
        }
        catch (ClassNotFoundException ex) {
        }
    }
}
