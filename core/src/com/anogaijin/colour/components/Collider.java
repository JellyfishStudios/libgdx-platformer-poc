package com.anogaijin.colour.components;

import com.anogaijin.colour.physics.contacts.ContactData;
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created by adunne on 2015/09/24.
 */
public class Collider implements Component {
    public Collider() {
        this(null);
    }

    public Collider(Body rigidBody) {
        this.rigidBody = rigidBody;

        contacts = new ObjectMap<>();
    }

    public ObjectMap<String, ContactData> contacts;
    public Body rigidBody;
}
