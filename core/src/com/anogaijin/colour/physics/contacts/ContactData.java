package com.anogaijin.colour.physics.contacts;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

/**
 * Created by adunne on 2015/10/24.
 */
public class ContactData {
    public Fixture fixtureA;
    public Fixture fixtureB;
    public boolean isTouching;
    private String contactId;

    ContactData (Contact contact) {
        this.fixtureA = contact.getFixtureA();
        this.fixtureB = contact.getFixtureB();
        this.isTouching = contact.isTouching();

        contactId = generageNewContactId(contact);
    }

    public String getContactId() {
        return contactId;
    }

    public static String generageNewContactId(Contact contact) {
        return String.valueOf(contact.getFixtureA().hashCode()) + String.valueOf(contact.getFixtureB().hashCode());
    }
}
