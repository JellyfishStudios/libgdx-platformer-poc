package com.anogaijin.colour.assets.loaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.*;
import com.esotericsoftware.spine.attachments.AtlasAttachmentLoader;


/**
 * Created by adunne on 2015/10/19.
 */
public class SkeletonDataLoader extends AsynchronousAssetLoader<SkeletonData, SkeletonDataLoader.SkeletonDataLoaderParameters> {
    private SkeletonData skeleton;

    public static class SkeletonDataLoaderParameters extends AssetLoaderParameters<SkeletonData> {
    }

    public SkeletonDataLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, SkeletonDataLoaderParameters parameter) {
        SkeletonJson json = new SkeletonJson
                (new AtlasAttachmentLoader(
                        manager.get(
                                String.format("%s/%s.atlas", file.parent().path(), file.nameWithoutExtension()),
                                TextureAtlas.class)));

        json.setScale(0.004f);

        skeleton = json.readSkeletonData(Gdx.files.internal(fileName));
    }

    @Override
    public SkeletonData loadSync(AssetManager manager, String fileName, FileHandle file, SkeletonDataLoaderParameters parameter) {
        return skeleton;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, SkeletonDataLoaderParameters parameter) {
        AssetDescriptor descriptor = new AssetDescriptor(
                String.format("%s/%s.atlas", file.parent().path(), file.nameWithoutExtension()),
                TextureAtlas.class);

        Array<AssetDescriptor> descriptors = new Array<>();
        descriptors.add(descriptor);

        return descriptors;
    }
}
