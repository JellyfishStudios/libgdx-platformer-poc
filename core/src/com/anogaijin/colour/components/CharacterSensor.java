package com.anogaijin.colour.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created by adunne on 2015/10/24.
 */
public class CharacterSensor implements Component {
    public boolean topIsTouching = false;
    public boolean bottomIsTouching = false;
    public boolean leftIsTouching = false;
    public boolean rightIsTouching = false;

    public ObjectMap<CharacterSensorType, Entity> entities = new ObjectMap<>();

    public enum CharacterSensorType {
        Top,
        Bottom,
        Left,
        Right,
        Unknown;
    }
}
