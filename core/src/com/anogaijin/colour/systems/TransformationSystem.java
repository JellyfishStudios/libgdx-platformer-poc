package com.anogaijin.colour.systems;

import com.anogaijin.colour.components.*;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

/**
 * Created by adunne on 2015/09/19.
 */
public class TransformationSystem extends IteratingSystem{
    ComponentMapper<RigidBody>  rbm = ComponentMapper.getFor(RigidBody.class);
    ComponentMapper<Transform>  tm = ComponentMapper.getFor(Transform.class);

    public TransformationSystem() {
        super(Family.all(RigidBody.class, Transform.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        RigidBody collider = rbm.get(entity);
        Transform transform = tm.get(entity);

        // Mirroring any rigid body changes for the entity
        //
        transform.position.x = collider.body.getPosition().x;
        transform.position.y = collider.body.getPosition().y;
        transform.rotation = (float) Math.toDegrees(collider.body.getAngle());
        transform.flipped = collider.flipped;
    }
}
