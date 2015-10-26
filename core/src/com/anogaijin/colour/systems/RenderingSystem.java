package com.anogaijin.colour.systems;

import com.anogaijin.colour.components.*;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.esotericsoftware.spine.SkeletonRenderer;

/**
 * Created by adunne on 2015/10/23.
 */
public class RenderingSystem extends EntitySystem {
    SpriteBatch batch;
    ImmutableArray<Entity> entities;
    OrthographicCamera camera;
    FitViewport viewport;
    SkeletonRenderer modelRenderer;

    ComponentMapper<Image> im = ComponentMapper.getFor(Image.class);
    ComponentMapper<Model> mm = ComponentMapper.getFor(Model.class);
    ComponentMapper<Transform> tm = ComponentMapper.getFor(Transform.class);

    public RenderingSystem() {
        camera = new OrthographicCamera();
        modelRenderer = new SkeletonRenderer();
        batch = new SpriteBatch();

        viewport = new FitViewport(14f, 14 * Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth(), camera);
        viewport.apply(true);
    }

    public void setCamera(OrthographicCamera camera) {
        this.camera = camera;
        this.viewport.setCamera(camera);
        this.viewport.apply(true);
    }

    public OrthographicCamera getCamera() {
        return this.camera;
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(
                Family
                        .all(Transform.class)
                        .one(Model.class, Image.class)
                        .get());
    }

    @Override
    public void update(float deltaTime) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (Entity entity : entities) {
            if (mm.has(entity))
                renderModel(entity, batch);

            if (im.has(entity))
                renderImage(entity, batch);
        }

        batch.end();
    }

    private void renderModel(Entity entity, SpriteBatch batch) {
        Model model = mm.get(entity);
        Transform transform = tm.get(entity);

        model.skeleton.setPosition(transform.position.x, transform.position.y);
        model.skeleton.getRootBone().setRotation(transform.rotation);
        model.skeleton.setFlipX(transform.flipped);
        model.skeleton.updateWorldTransform();

        modelRenderer.draw(batch, model.skeleton);
    }

    private void renderImage(Entity entity, SpriteBatch batch) {
        Image visual = im.get(entity);
        Transform transform = tm.get(entity);

        batch.draw(
                visual.texture,
                transform.position.x - (visual.width / 2),
                transform.position.y - (visual.height / 2),
                visual.width / 2,
                visual.height / 2,
                visual.width,
                visual.height,
                transform.scale.x,
                transform.scale.y,
                transform.rotation);
    }
}
