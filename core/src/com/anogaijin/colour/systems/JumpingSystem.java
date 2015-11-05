package com.anogaijin.colour.systems;

import com.anogaijin.colour.components.*;
import com.anogaijin.colour.physics.PhysicsUtil;
import com.anogaijin.colour.systems.states.CharacterState;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

/**
 * Created by adunne on 2015/09/24.
 */
public class JumpingSystem extends IteratingSystem {
    ComponentMapper<Collider> rbm = ComponentMapper.getFor(Collider.class);
    ComponentMapper<Input> im = ComponentMapper.getFor(Input.class);
    ComponentMapper<Motion> mm = ComponentMapper.getFor(Motion.class);
    ComponentMapper<Brain> bm = ComponentMapper.getFor(Brain.class);
    ComponentMapper<Jump> jm = ComponentMapper.getFor(Jump.class);

    public JumpingSystem() {
        super(Family.all(
                Collider.class, Motion.class, Jump.class, KeyboardController.class, Animation.class, Brain.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Collider collider = rbm.get(entity);
        Input input = im.get(entity);
        Motion motion = mm.get(entity);
        Brain brain = bm.get(entity);
        Jump jump = jm.get(entity);

        // Process input
        //
        if (brain.movement.getCurrentState() != CharacterState.Jumping) {
            if (input.JUMP) {
                // Inform the brain of what we're about to do
                //
                brain.movement.changeState(CharacterState.Jumping);

                // set desired movement
                motion.velocity.set(motion.velocity.x, jump.terminalVelocity);

                // Calculates required force to reach desired velocity in a given time-step
                //
                motion.force.y = PhysicsUtil.calculateRequiredImpulse(
                        collider.body.getMass(),
                        motion.velocity.y,
                        collider.body.getLinearVelocity().y);

                if (motion.force.y == 0f)
                    return;

                // Apply impulse!
                //
                collider.body.applyLinearImpulse(
                        0f,
                        motion.force.y,
                        collider.body.getWorldCenter().x,
                        collider.body.getWorldCenter().y,
                        true);
            }
        }
        else
        {
            // Check if we've reached the apex of the jump
            //
            if (collider.body.getLinearVelocity().y <= 0f)
                brain.movement.changeState(CharacterState.Falling);
        }
    }


}
