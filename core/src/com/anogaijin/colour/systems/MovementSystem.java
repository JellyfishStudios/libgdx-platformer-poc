package com.anogaijin.colour.systems;

import com.anogaijin.colour.components.*;
import com.anogaijin.colour.physics.PhysicsUtil;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.esotericsoftware.spine.attachments.BoundingBoxAttachment;

/**
 * Created by adunne on 2015/09/24.
 */
public class MovementSystem extends IteratingSystem {
    ComponentMapper<RigidBody> rbm = ComponentMapper.getFor(RigidBody.class);
    ComponentMapper<Controller> cm = ComponentMapper.getFor(Controller.class);
    ComponentMapper<Motion> mm = ComponentMapper.getFor(Motion.class);
    ComponentMapper<Brain> bm = ComponentMapper.getFor(Brain.class);

    public MovementSystem() {
        super(Family.all(RigidBody.class, Motion.class, Controller.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        RigidBody collider = rbm.get(entity);
        Controller controller = cm.get(entity);
        Motion motion = mm.get(entity);

        if (Gdx.input.isKeyPressed(controller.RIGHT)) {
            motion.velocity.set(motion.fixedStep.x, motion.velocity.y);

            if (collider.flipped)
                flipRigidBody(collider);
        }
        else if (Gdx.input.isKeyPressed(controller.LEFT)) {
            motion.velocity.set(-motion.fixedStep.x, motion.velocity.y);

            if (!collider.flipped)
                flipRigidBody(collider);
        }
        else {
            motion.velocity.set(0f, motion.velocity.y);
        }

        // Calculates required force to reach desired velocity in a given time-step
        //
        motion.force.x = PhysicsUtil.calculateRequiredForce(
                collider.body.getMass(),
                motion.velocity.x,
                collider.body.getLinearVelocity().x,
                1 / 60f);

        if (motion.force.x == 0f)
            return;

        // Apply force!
        //
        collider.body.applyForceToCenter(motion.force.x, 0f, true);
    }

    private void flipRigidBody(RigidBody rigidBody) {
        rigidBody.flipped = !rigidBody.flipped;

        for (Fixture fixture : rigidBody.body.getFixtureList()) {
            // We don't need to flip the capsule
            //
            BoundingBoxAttachment attachment = (BoundingBoxAttachment)fixture.getUserData();
            if (attachment.getName().endsWith("capsule"))
                continue;

            PolygonShape polygon = (PolygonShape)fixture.getShape();
            Vector2 tmpVector = new Vector2();
            for (int i = 0; i < polygon.getVertexCount(); i++) {
                polygon.getVertex(i, tmpVector);
                tmpVector.set(-tmpVector.x, tmpVector.y);
            }
        }
    }
}
