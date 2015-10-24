package com.anogaijin.colour.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.spine.Skeleton;

/**
 * Created by adunne on 2015/10/23.
 */
public class Model implements Component {
    public Model(Skeleton skeleton) {
        this.skeleton = skeleton;
    }

    public Skeleton skeleton;
}
