package com.anogaijin.colour.systems;

import com.anogaijin.colour.components.*;
import com.anogaijin.colour.physics.PhysicsUtil;
import com.anogaijin.colour.systems.states.CharacterState;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.esotericsoftware.spine.attachments.BoundingBoxAttachment;

/**
 * Created by adunne on 2015/09/24.
 */
public class WalkingSystem extends IteratingSystem {
    ComponentMapper<Collider> rbm = ComponentMapper.getFor(Collider.class);
    ComponentMapper<Input> im = ComponentMapper.getFor(Input.class);
    ComponentMapper<Motion> mm = ComponentMapper.getFor(Motion.class);
    ComponentMapper<Brain> bm = ComponentMapper.getFor(Brain.class);
    ComponentMapper<Walk> wm = ComponentMapper.getFor(Walk.class);

    public WalkingSystem() {
        super(Family.all(Walk.class, Collider.class, Motion.class, KeyboardController.class, Brain.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Collider collider = rbm.get(entity);
        Input input = im.get(entity);
        Motion motion = mm.get(entity);
        Walk walk = wm.get(entity);
        Brain brain = bm.get(entity);

        // If we've fallen hand this off to another state
        //
        if (!PhysicsUtil.isMySensorTouching(PhysicsUtil.BOTTOM_SENSOR, collider.getContacts())) {
            brain.movement.changeState(CharacterState.Falling);

            return;
        }

        if (input.MOVE_RIGHT) {
            motion.velocity.set(walk.terminalVelocity, motion.velocity.y);

            brain.movement.changeState(CharacterState.Walking);

            if (collider.flipped) {
                flipRigidBody(collider);
            }
        }
        else if (input.MOVE_LEFT) {
            motion.velocity.set(-walk.terminalVelocity, motion.velocity.y);

            brain.movement.changeState(CharacterState.Walking);

            if (!collider.flipped)
                flipRigidBody(collider);
        }
        else {
            motion.velocity.set(0f, motion.velocity.y);

            brain.movement.changeState(CharacterState.Idle);
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

    private void flipRigidBody(Collider collider) {
        collider.flipped = !collider.flipped;

        for (Fixture fixture : collider.body.getFixtureList()) {
            // We don't need to flip the capsule
            //
            BoundingBoxAttachment attachment = (BoundingBoxAttachment)fixture.getUserData();
            if (attachment.getName().endsWith("capsule"))
                continue;

            PolygonShape polygon = (PolygonShape)fixture.getShape();
            Vector2[] vertices = new Vector2[polygon.getVertexCount()];
            for (int i = 0; i < polygon.getVertexCount(); i++) {
                vertices[i] = new Vector2();

                polygon.getVertex(i, vertices[i]);
                vertices[i].set(-vertices[i].x, vertices[i].y);
            }

            polygon.set(vertices);
        }
    }
}
