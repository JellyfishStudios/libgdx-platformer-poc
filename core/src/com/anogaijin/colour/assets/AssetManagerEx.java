package com.anogaijin.colour.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.*;

import java.lang.reflect.Constructor;

/**
 * Created by adunne on 2015/10/19.
 */
public class AssetManagerEx implements Disposable{
    String file;
    AssetManager manager;
    Json json;

    ObjectMap<String, Array<AssetDefinition>> groups;
    ObjectMap<String, AssetDefinition> assets;

    public AssetManagerEx(AssetManager manager, String file) {
        this.file = file;
        this.manager = manager;

        json = new Json();
        json.setTypeName(null);
        json.setUsePrototypes(false);

        groups = new ObjectMap<>();
        assets = new ObjectMap<>();

        initialiseLoaders();
    }

    private void initialiseLoaders() {
        JsonReader jsonReader = new JsonReader();
        Array<LoaderDefinition> loaderDefs = json.readValue("loaders", Array.class, LoaderDefinition.class, jsonReader.parse(new FileHandle(file)));

        for (LoaderDefinition loaderDef : loaderDefs) {
            try {
                Constructor<? extends AssetLoader> constructor = loaderDef.loader.getConstructor(FileHandleResolver.class);
                manager.setLoader(loaderDef.type, constructor.newInstance(new InternalFileHandleResolver()));

            } catch (NoSuchMethodException ex) {
            } catch (Exception ex) {
            }
        }
    }

    public void load(String group) {
        if (groups.containsKey(group))
            return;

        JsonReader jsonReader = new JsonReader();
        for (JsonValue jsonNode : jsonReader.parse(new FileHandle(file)).get("groups").iterator()) {
            if (jsonNode.getString("name").equals(group)) {
                Array<AssetDefinition> assetDefs = json.readValue("assets", Array.class, AssetDefinition.class, jsonNode);
                if (assetDefs.size > 0) {
                    for (AssetDefinition assetDef : assetDefs) {
                        assets.put(assetDef.alias, assetDef);
                        manager.load(assetDef.path, assetDef.type);
                    }

                    groups.put(group, assetDefs);
                }
            }
        }
    }

    public void unload(String group) {
        if (!groups.containsKey(group))
            return;

        for (AssetDefinition assetDef : groups.get(group)) {
            assets.remove(assetDef.alias);
            manager.unload(assetDef.path);
        }

        groups.remove(group);
    }

    public <T> T get(String alias) {
        if (!assets.containsKey(alias))
            return null;

        return manager.get(assets.get(alias).path);
    }

    public <T> T get(String alias, Class<T> type) {
        if (!assets.containsKey(alias))
            return null;

        return manager.get(assets.get(alias).path, type);
    }

    public AssetManager getManager() {
        return manager;
    }

    public void update() {
        manager.update();
    }

    public void finishLoading() {
        manager.finishLoading();
    }

    public float getProgress() {
        return manager.getProgress();
    }

    public String getDiagnostics() {
        return manager.getDiagnostics();
    }

    @Override
    public void dispose() {
        manager.dispose();
    }
}
