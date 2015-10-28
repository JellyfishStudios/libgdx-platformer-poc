package com.anogaijin.colour.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by adunne on 2015/09/24.
 */
public class Motion implements Component {
    public Vector2 velocity = new Vector2(0f, 0f);
    public Vector2 force = new Vector2(0f, 0f);
}
