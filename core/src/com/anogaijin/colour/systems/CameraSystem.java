package com.anogaijin.colour.systems;

import com.anogaijin.colour.components.*;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

/**
 * Created by adunne on 2015/10/25.
 */
public class CameraSystem extends IteratingSystem {
    ComponentMapper<Camera> cm = ComponentMapper.getFor(Camera.class);
    ComponentMapper<Transform> tm = ComponentMapper.getFor(Transform.class);

    public CameraSystem() {
        super(Family.all(Camera.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Camera cam = cm.get(entity);

        if (cam.target == null)
            return;

        float timeStep = deltaTime;
        if (cam.instantTracking)
            timeStep = 1f;

        Transform targetTransform = tm.get(cam.target);
        cam.camera.translate(
                (targetTransform.position.x - (cam.camera.position.x - cam.trackingOffset.x)) * timeStep,
                (targetTransform.position.y - (cam.camera.position.y - cam.trackingOffset.y)) * timeStep);

        cam.camera.update();
    }
}
