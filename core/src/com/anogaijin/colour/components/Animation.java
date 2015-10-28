package com.anogaijin.colour.components;

import com.badlogic.ashley.core.Component;
import com.esotericsoftware.spine.AnimationState;

/**
 * Created by adunne on 2015/10/26.
 */
public class Animation implements Component {
    public AnimationState state;

    public Animation(AnimationState state) {
        this.state = state;
    }

    public void setState(int track, String animation, boolean loop) {
        if (state.getCurrent(track) == null || !state.getCurrent(track).getAnimation().getName().equals(animation))
            state.setAnimation(track, animation, loop);
    }
}
