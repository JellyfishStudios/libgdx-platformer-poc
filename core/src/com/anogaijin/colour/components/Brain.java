package com.anogaijin.colour.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.State;

/**
 * Created by adunne on 2015/09/20.
 */
public class Brain<T> implements Component{
    public Brain(T owner, State<T> initialMovementState) {
        movement = new DefaultStateMachine<T>(owner);
        movement.changeState(initialMovementState);
    }

    public DefaultStateMachine<T> movement;
}
