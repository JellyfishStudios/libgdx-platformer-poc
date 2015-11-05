package com.anogaijin.colour.components;

import com.anogaijin.colour.physics.contacts.ContactData;
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created by adunne on 2015/09/24.
 */
public class Collider implements Component {
    public boolean flipped = false;
    public ObjectMap<String, ContactData> contacts = new ObjectMap<>();
    public Body body;

    public Collider(Body collider) {
        this.body = collider;
    }

    public Body getRigidBody() {
        return body;
    }

    public Array<ContactData> getContacts() {
        return contacts.values().toArray();
    }
}
