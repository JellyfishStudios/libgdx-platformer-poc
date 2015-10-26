package com.anogaijin.colour.systems;

import com.anogaijin.colour.components.*;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.BodyDef;

/**
 * Created by adunne on 2015/09/19.
 */
public class TransformationSystem extends IteratingSystem{
    ComponentMapper<Collider>  rbm = ComponentMapper.getFor(Collider.class);
    ComponentMapper<Transform>  tm = ComponentMapper.getFor(Transform.class);

    public TransformationSystem() {
        super(Family.all(Collider.class, Transform.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Collider collider = rbm.get(entity);
        Transform transform = tm.get(entity);

        // Mirroring any rigid body changes for the entity
        //
        transform.position.x = collider.rigidBody.getPosition().x;
        transform.position.y = collider.rigidBody.getPosition().y;
        transform.rotation = (float) Math.toDegrees(collider.rigidBody.getAngle());
    }
}
