package com.anogaijin.colour;

import com.anogaijin.colour.assets.AssetManagerEx;
import com.anogaijin.colour.entities.EntityManager;
import com.anogaijin.colour.physics.contacts.ContactListener;
import com.anogaijin.colour.systems.RenderingSystem;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

public class Colour extends ApplicationAdapter {
	Engine ecsEngine;
	AssetManagerEx assetManager;
	EntityManager entityManager;
	Box2DDebugRenderer b2dr;
	
	@Override
	public void create () {
		ecsEngine = new Engine();
		b2dr = new Box2DDebugRenderer();

		assetManager = new AssetManagerEx(new AssetManager(new InternalFileHandleResolver()), "assets.json");
		assetManager.load("test");
		assetManager.finishLoading();

		entityManager = new EntityManager(ecsEngine, assetManager);
		entityManager.initialiseSystems();
		entityManager.initialiseSceneEntities();
		entityManager.initialiseCharactersEntities();
		entityManager.getPhysicsWorld().setContactListener(new ContactListener());
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		entityManager.getPhysicsWorld().step(1/60f, 8, 3);
		ecsEngine.update(Gdx.graphics.getDeltaTime());

		b2dr.render(entityManager.getPhysicsWorld(), ecsEngine.getSystem(RenderingSystem.class).getCamera().combined);
	}
}
