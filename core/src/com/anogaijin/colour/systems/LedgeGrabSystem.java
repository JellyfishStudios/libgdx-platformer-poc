package com.anogaijin.colour.systems;

import com.anogaijin.colour.components.*;
import com.anogaijin.colour.entities.EntityManager;
import com.anogaijin.colour.physics.PhysicsUtil;
import com.anogaijin.colour.physics.Ray;
import com.anogaijin.colour.physics.contacts.ContactData;
import com.anogaijin.colour.systems.states.CharacterState;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;

/**
 * Created by adunne on 2015/10/30.
 */
public class LedgeGrabSystem extends IteratingSystem {
    ComponentMapper<Collider> rbm = ComponentMapper.getFor(Collider.class);
    ComponentMapper<Input> im = ComponentMapper.getFor(Input.class);
    ComponentMapper<Brain> bm = ComponentMapper.getFor(Brain.class);
    ComponentMapper<Grab> gm = ComponentMapper.getFor(Grab.class);

    ShapeRenderer shapeDebugger;
    Ray ray = new Ray();

    public LedgeGrabSystem() {
        super(Family
                .all(Grab.class, Input.class, Motion.class, Brain.class, Collider.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Collider collider = rbm.get(entity);
        Input input = im.get(entity);
        Brain brain = bm.get(entity);
        Grab grab = gm.get(entity);

        if (brain.movement.getCurrentState() == CharacterState.Grabbing) {
            if (grab.wallJoint != null) {
                if (input.DROP) {
                    brain.movement.changeState(CharacterState.Falling);
                    Gdx.app.log("Grab", "Letting go!");

                    collider.body.getWorld().destroyJoint(grab.wallJoint);
                    grab.grabReleased = System.currentTimeMillis();
                }
            }

            return;
        }

        ContactData contact = PhysicsUtil.getMyTouchingSensorContact(PhysicsUtil.UPPER_FRONT_SENSOR, collider.getContacts());
        if (contact != null) {
            if (System.currentTimeMillis() - grab.grabReleased < grab.grabLag)
                return;

            PolygonShape poly = (PolygonShape)contact.myFixture.getShape();

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
            end.set(start.x + (grab.rayLength * characterDirection), start.y);

            Gdx.app.log(
                    "Grab",
                    "Ray casting from: " + start.x + ", " + start.y + " to: " + end.x + ", " + end.y );

            shapeDebugger = new ShapeRenderer();
            shapeDebugger.setProjectionMatrix(EntityManager.ecsEngine.getSystem(RenderingSystem.class).getCamera().combined);
            shapeDebugger.begin(ShapeRenderer.ShapeType.Line);
            shapeDebugger.setColor(0, 0, 0, 0);
            shapeDebugger.line(start, end);
            shapeDebugger.end();

            if (!ray.cast(collider.body.getWorld(), start, end, EntityManager.CATEGORY_OBJECTS)) {
                brain.movement.changeState(CharacterState.Grabbing);
                Gdx.app.log("Grab!", "Grabbed!");

                Vector2 anchorA = new Vector2();
                anchorA.set(start.x, start.y);

                Vector2 anchorB = new Vector2();
                anchorB.set(start.x + (0.1f * characterDirection), start.y);

                Gdx.app.log(
                        "Anchor!",
                        "Anchor A: " + anchorA.x + ", " + anchorA.y + " Anchor B: " + anchorB.x + ", " + anchorB.y );

                DistanceJointDef jointDef = new DistanceJointDef();
                jointDef.initialize(contact.myFixture.getBody(), contact.otherFixture.getBody(), anchorA, anchorB);
                jointDef.collideConnected = true;

                grab.wallJoint = (DistanceJoint)contact.myFixture.getBody().getWorld().createJoint(jointDef);
            }
        }
    }
}
