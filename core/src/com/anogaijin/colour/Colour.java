package com.anogaijin.colour;

import com.anogaijin.colour.assets.AssetDefinition;
import com.anogaijin.colour.assets.AssetManagerEx;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonRenderer;

public class Colour extends ApplicationAdapter {
	SpriteBatch batch;
	Skeleton skeleton;
	Skeleton skeleton2;
	Skeleton skeleton3;
	SkeletonRenderer renderer;
	OrthographicCamera mainCam;
	FitViewport viewPort;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		renderer = new SkeletonRenderer();

		mainCam = new OrthographicCamera();

		viewPort = new FitViewport(10f, 10 * Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth(), mainCam);
		viewPort.apply(true);

		AssetManagerEx assetManager = new AssetManagerEx(new AssetManager(new InternalFileHandleResolver()), "final/assets.json");
		assetManager.load("test");
		assetManager.finishLoading();

		skeleton = new Skeleton(assetManager.get("heroModel", SkeletonData.class));
		skeleton.setPosition(5f, 2f);
		skeleton.updateWorldTransform();

		skeleton2 = new Skeleton(assetManager.get("heroModel", SkeletonData.class));
		skeleton2.setPosition(1f, 1f);
		skeleton2.updateWorldTransform();

		skeleton3 = new Skeleton(assetManager.get("heroModel", SkeletonData.class));
		skeleton3.setPosition(3f, 1.5f);
		skeleton3.updateWorldTransform();

		Gdx.app.log("Diag", "" + assetManager.getDiagnostics());
	}

	@Override
	public void render () {
		mainCam.update();

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(mainCam.combined);
		batch.begin();
		renderer.draw(batch, skeleton);
		renderer.draw(batch, skeleton2);
		renderer.draw(batch, skeleton3);
		batch.end();
	}
}
