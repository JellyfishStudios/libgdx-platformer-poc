package com.anogaijin.colour.systems;

import com.anogaijin.colour.components.*;
import com.anogaijin.colour.physics.PhysicsUtil;
import com.anogaijin.colour.systems.states.CharacterState;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;

/**
 * Created by adunne on 2015/09/24.
 */
public class JumpingSystem extends IteratingSystem {
    ComponentMapper<RigidBody> rbm = ComponentMapper.getFor(RigidBody.class);
    ComponentMapper<KeyboardController> cm = ComponentMapper.getFor(KeyboardController.class);
    ComponentMapper<Motion> mm = ComponentMapper.getFor(Motion.class);
    ComponentMapper<Brain> bm = ComponentMapper.getFor(Brain.class);
    ComponentMapper<Jump> jm = ComponentMapper.getFor(Jump.class);
    ComponentMapper<CharacterSensor> sm = ComponentMapper.getFor(CharacterSensor.class);

    public JumpingSystem() {
        super(Family.all(RigidBody.class, Motion.class, Jump.class, KeyboardController.class, Animation.class, Brain.class, CharacterSensor.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        RigidBody rigidBody = rbm.get(entity);
        KeyboardController controller = cm.get(entity);
        Motion motion = mm.get(entity);
        Brain brain = bm.get(entity);
        Jump jump = jm.get(entity);

        // Process input
        //
        if (brain.movement.getCurrentState() != CharacterState.Jumping) {
            if (Gdx.input.isKeyJustPressed(controller.UP)) {
                // Inform the brain of what we're about to do
                //
                brain.movement.changeState(CharacterState.Jumping);

                // set desired movement
                motion.velocity.set(motion.velocity.x, jump.terminalVelocity);

                // Calculates required force to reach desired velocity in a given time-step
                //
                motion.force.y = PhysicsUtil.calculateRequiredImpulse(
                        rigidBody.body.getMass(),
                        motion.velocity.y,
                        rigidBody.body.getLinearVelocity().y);

                if (motion.force.y == 0f)
                    return;

                // Apply impulse!
                //
                rigidBody.body.applyLinearImpulse(
                        0f,
                        motion.force.y,
                        rigidBody.body.getWorldCenter().x,
                        rigidBody.body.getWorldCenter().y,
                        true);
            }
        }
        else
        {
            // Check if we've reached the apex of the jump
            //
            if (rigidBody.body.getLinearVelocity().y <= 0f)
                brain.movement.changeState(CharacterState.Falling);
        }
    }


}
