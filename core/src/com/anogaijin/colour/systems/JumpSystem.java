package com.anogaijin.colour.systems;

import com.anogaijin.colour.components.*;
import com.anogaijin.colour.physics.PhysicsUtil;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;

/**
 * Created by adunne on 2015/09/24.
 */
public class JumpSystem extends IteratingSystem {
    ComponentMapper<Collider> rbm = ComponentMapper.getFor(Collider.class);
    ComponentMapper<Controller> cm = ComponentMapper.getFor(Controller.class);
    ComponentMapper<Motion> mm = ComponentMapper.getFor(Motion.class);
    ComponentMapper<Brain> bm = ComponentMapper.getFor(Brain.class);

    public JumpSystem() {
        super(Family.all(Collider.class, Motion.class, Jump.class, Controller.class, Brain.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Collider collider = rbm.get(entity);
        Controller controller = cm.get(entity);
        Motion motion = mm.get(entity);
        Brain brain = bm.get(entity);

        if (Gdx.input.isKeyJustPressed(controller.UP)) {
            motion.velocity.set(motion.velocity.x, motion.fixedStep.y);

            // Calculates required force to reach desired velocity in a given time-step
            //
            motion.force.y = PhysicsUtil.calculateRequiredImpulse(
                    collider.rigidBody.getMass(),
                    motion.velocity.y,
                    collider.rigidBody.getLinearVelocity().y);

            // Apply impulse!
            //
            collider.rigidBody.applyLinearImpulse(
                    0f,
                    motion.force.y,
                    collider.rigidBody.getWorldCenter().x,
                    collider.rigidBody.getWorldCenter().y,
                    true);
        }
    }


}
