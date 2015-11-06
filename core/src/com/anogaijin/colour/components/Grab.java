package com.anogaijin.colour.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;

import java.util.Date;

/**
 * Created by adunne on 2015/11/02.
 */
public class Grab implements Component {
    public DistanceJoint wallJoint;
    public long grabLag = 500;      // (ms) delay from the moment grab is released, after which grab is re-enabled
    public long grabReleased = 0;   // (ms) should be set to current time (ms) when a grab is released
    public float rayLength = 0.2f;  // (m) how much of a gap to check for (keep this as small as possible for climbing ladders, etc.)
}
