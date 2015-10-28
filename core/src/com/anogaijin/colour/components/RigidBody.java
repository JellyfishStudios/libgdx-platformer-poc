package com.anogaijin.colour.components;

import com.anogaijin.colour.physics.contacts.ContactData;
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created by adunne on 2015/09/24.
 */
public class RigidBody implements Component {
    public RigidBody(Body rigidBody) {
        this.body = rigidBody;
    }

    public boolean flipped = false;
    public ObjectMap<String, ContactData> contacts = new ObjectMap<>();
    public Body body;

}
