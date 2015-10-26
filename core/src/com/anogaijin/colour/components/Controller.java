package com.anogaijin.colour.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/**
 * Created by adunne on 2015/09/24.
 */
public class Controller implements Component {
    public int UP = Input.Keys.UP;
    public int DOWN = Input.Keys.DOWN;
    public int LEFT = Input.Keys.LEFT;
    public int RIGHT = Input.Keys.RIGHT;

    public int SPIN_LEFT = Input.Keys.LEFT_BRACKET;
    public int SPIN_RIGHT = Input.Keys.RIGHT_BRACKET;
}
