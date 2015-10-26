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
    ComponentMapper<RigidBody> rbm = ComponentMapper.getFor(RigidBody.class);
    ComponentMapper<Controller> cm = ComponentMapper.getFor(Controller.class);
    ComponentMapper<Motion> mm = ComponentMapper.getFor(Motion.class);
    ComponentMapper<Brain> bm = ComponentMapper.getFor(Brain.class);

    public JumpSystem() {
        super(Family.all(RigidBody.class, Motion.class, Jump.class, Controller.class, Brain.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        RigidBody collider = rbm.get(entity);
        Controller controller = cm.get(entity);
        Motion motion = mm.get(entity);
        Brain brain = bm.get(entity);

        if (Gdx.input.isKeyJustPressed(controller.UP)) {
            motion.velocity.set(motion.velocity.x, motion.fixedStep.y);

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


}
