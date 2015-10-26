package com.anogaijin.colour.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by adunne on 2015/10/25.
 */
public class Camera implements Component{
    public Camera(OrthographicCamera camera) {
        this.camera = camera;
        this.trackingOffset = new Vector2(2f, 2.5f);
        this.instantTracking = false;
    }

    public OrthographicCamera camera;
    public Vector2 trackingOffset;
    public boolean instantTracking;
}
