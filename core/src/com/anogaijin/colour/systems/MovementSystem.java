package com.anogaijin.colour.systems;

import com.anogaijin.colour.components.*;
import com.anogaijin.colour.physics.PhysicsUtil;
import com.anogaijin.colour.systems.states.PlayerState;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;

/**
 * Created by adunne on 2015/09/24.
 */
public class MovementSystem extends IteratingSystem {
    ComponentMapper<Collider> rbm = ComponentMapper.getFor(Collider.class);
    ComponentMapper<Controller> cm = ComponentMapper.getFor(Controller.class);
    ComponentMapper<Motion> mm = ComponentMapper.getFor(Motion.class);
    ComponentMapper<Brain> bm = ComponentMapper.getFor(Brain.class);

    public MovementSystem() {
        super(Family.all(Collider.class, Motion.class, Controller.class, Brain.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Collider collider = rbm.get(entity);
        Controller controller = cm.get(entity);
        Motion motion = mm.get(entity);
        Brain brain = bm.get(entity);

        if (Gdx.input.isKeyPressed(controller.RIGHT)) {
            motion.velocity.set(motion.fixedStep.x, motion.velocity.y);
        }
        else if (Gdx.input.isKeyPressed(controller.LEFT)) {
            motion.velocity.set(-motion.fixedStep.x, motion.velocity.y);
        }
        else {
            motion.velocity.set(0f, motion.velocity.y);
        }

        // Calculates required force to reach desired velocity in a given time-step
        //
        motion.force.x = PhysicsUtil.calculateRequiredForce(
                collider.rigidBody.getMass(),
                motion.velocity.x,
                collider.rigidBody.getLinearVelocity().x,
                1 / 60f);

        // Apply force!
        //
        collider.rigidBody.applyForceToCenter(motion.force.x, 0f, true);
    }
}
