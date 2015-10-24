package com.anogaijin.colour.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by adunne on 2015/09/19.
 */
public class Transform implements Component {
    public Transform() {
        this(new Vector2(),new Vector2(1.0f, 1.0f), 0f);
    }

    public Transform(Vector2 position, Vector2 scale, float rotation) {
        this.position = position;
        this.scale = scale;
        this.rotation = rotation;
    }

    public Vector2 position;
    public Vector2 scale;
    public float rotation;
}
