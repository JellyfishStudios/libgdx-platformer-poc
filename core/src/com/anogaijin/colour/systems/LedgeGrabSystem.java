package com.anogaijin.colour.systems;

import com.anogaijin.colour.components.*;
import com.anogaijin.colour.entities.EntityManager;
import com.anogaijin.colour.physics.PhysicsUtil;
import com.anogaijin.colour.systems.states.CharacterState;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by adunne on 2015/10/30.
 */
public class LedgeGrabSystem extends IteratingSystem {
    ComponentMapper<Collider> rbm = ComponentMapper.getFor(Collider.class);
    ComponentMapper<Input> im = ComponentMapper.getFor(Input.class);
    ComponentMapper<Brain> bm = ComponentMapper.getFor(Brain.class);
    ComponentMapper<Climb> cm = ComponentMapper.getFor(Climb.class);

    ShapeRenderer shapeDebugger;

    public LedgeGrabSystem() {
        super(Family
                .all(Grab.class, Input.class, Motion.class, Brain.class, Collider.class)
                .one(Climb.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Collider collider = rbm.get(entity);
        Input input = im.get(entity);
        Brain brain = bm.get(entity);

        if (brain.movement.getCurrentState() == CharacterState.Grabbing) {
            if (input.DROP) {
                Gdx.app.log("Grab", "Letting go!");
            }

            if (input.JUMP) {
                if (!cm.has(entity))
                    return;

                Gdx.app.log("Grab", "Trying for a pull up!");
            }

            return;
        }

        Fixture myFixture = PhysicsUtil.getContactingSensor(entity, PhysicsUtil.UPPER_FRONT_SENSOR, collider.getContacts());
        if (myFixture != null) {
            PolygonShape poly = (PolygonShape) myFixture.getShape();

            Vector2 tmp = new Vector2();
            Vector2 highestPoint = new Vector2();
            for (int i = 0; i < poly.getVertexCount(); i++) {
                poly.getVertex(i, tmp);

                if (tmp.y > highestPoint.y)
                    highestPoint.set(tmp);
            }

            highestPoint = collider.body.getWorldPoint(highestPoint);

            // Any calculations need to be made based on the characters direction
            //
            float characterDirection = 1;
            if (collider.flipped)
                characterDirection = -1;

            Vector2 start = new Vector2();
            start.set(highestPoint.x - (0.1f * characterDirection), highestPoint.y);

            Vector2 end = new Vector2();
            end.set(start.x + (0.5f * characterDirection), start.y);

            Gdx.app.log(
                    "Grab",
                    "Ray casting from: " + start.x + ", " + start.y + " to: " + end.x + ", " + end.y );

            shapeDebugger = new ShapeRenderer();
            shapeDebugger.setProjectionMatrix(EntityManager.ecsEngine.getSystem(RenderingSystem.class).getCamera().combined);
            shapeDebugger.begin(ShapeRenderer.ShapeType.Line);
            shapeDebugger.setColor(0, 0, 0, 0);
            shapeDebugger.line(start, end);
            shapeDebugger.end();

            Ray ray = new Ray(collider.body.getWorld());
            if (!ray.cast(start, end, EntityManager.CATEGORY_OBJECTS)) {
                //brain.movement.changeState(CharacterState.Grabbing);

                Gdx.app.log("Grab", "Grabbed!");
            }
        }
    }

    private class Ray implements RayCastCallback {
        private boolean hasHit = false;
        private short filter;
        private World world;

        public Ray(World world) {
            this.world = world;
        }

        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            if ((fixture.getFilterData().categoryBits & filter) != 0) {
                hasHit = true;
                return 0;
            }

            return 1;
        }

        public boolean cast (Vector2 start, Vector2 end, short filter) {
            this.filter = filter;
            world.rayCast(this, start, end);

            return hasHit;
        }
    }
}
