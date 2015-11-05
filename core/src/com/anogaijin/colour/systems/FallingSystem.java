package com.anogaijin.colour.systems;

import com.anogaijin.colour.components.*;
import com.anogaijin.colour.physics.PhysicsUtil;
import com.anogaijin.colour.systems.states.CharacterState;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

/**
 * Created by adunne on 2015/10/27.
 */
public class FallingSystem extends IteratingSystem {
    ComponentMapper<Brain> bm = ComponentMapper.getFor(Brain.class);
    ComponentMapper<Collider> cm = ComponentMapper.getFor(Collider.class);

    public FallingSystem() {
        super(Family.all(Brain.class, Collider.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Brain brain = bm.get(entity);
        Collider collider = cm.get(entity);

        if (PhysicsUtil.isSensorContacting(entity, PhysicsUtil.BOTTOM_SENSOR, collider.getContacts())) {
            // Ensure we reset the state if necessary, since we've already landed
            //
            if (brain.movement.getCurrentState() == CharacterState.Falling)
                brain.movement.changeState(CharacterState.Idle);
        }
    }
}
