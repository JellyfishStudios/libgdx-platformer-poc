package com.anogaijin.colour.assets;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Created by adunne on 2015/10/19.
 */
public class AssetDefinition implements Json.Serializable {
    public String alias;
    public String path;
    public Class<?> type;

    @Override
    public void write(Json json) {

    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        alias = json.readValue("alias", String.class, jsonData);
        path = json.readValue("path", String.class, jsonData);

        try {
            type = Class.forName(json.readValue("type", String.class, jsonData));
        }
        catch (ClassNotFoundException ex) {
        }
    }
}