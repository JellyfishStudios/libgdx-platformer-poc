package com.anogaijin.colour.physics.contacts;

import com.anogaijin.colour.components.*;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * Created by adunne on 2015/09/19.
 */
public class ContactListener implements com.badlogic.gdx.physics.box2d.ContactListener {
    ComponentMapper<Collider> rbm = ComponentMapper.getFor(Collider.class);

    @Override
    public void beginContact(Contact contact) {
        // We need to be able to identify the colliding bodies
        //
        if (contact.getFixtureA().getBody().getUserData() == null ||
                contact.getFixtureA().getBody().getUserData() == null)
            return;

        Entity entityA = (Entity) contact.getFixtureA().getBody().getUserData();
        Entity entityB = (Entity) contact.getFixtureB().getBody().getUserData();

        // Don't process if it's part of the same entity
        //
        if (entityA.equals(entityB))
            return;

        Collider a = rbm.get(entityA);
        Collider b = rbm.get(entityB);

        ContactData contactData = new ContactData(contact);

        a.contacts.put(contactData.getContactId(), contactData);
        b.contacts.put(contactData.getContactId(), contactData);

        //Gdx.app.log("BEGIN COL", "Contact Hash " + contact.toString());
        //Gdx.app.log("BEGIN COL", "Fix A: " + "null" + " Collides with Fix B: " + contactData.fixtureB.getUserData().toString());
    }

    @Override
    public void endContact(Contact contact) {
        // We need to be able to identify the colliding bodies
        //
        if (contact.getFixtureA().getBody().getUserData() == null ||
                contact.getFixtureA().getBody().getUserData() == null)
            return;

        Entity entityA = (Entity) contact.getFixtureA().getBody().getUserData();
        Entity entityB = (Entity) contact.getFixtureB().getBody().getUserData();

        // Don't process if it's part of the same entity
        //
        if (entityA.equals(entityB))
            return;

        Collider a = rbm.get(entityA);
        Collider b = rbm.get(entityB);

        a.contacts.remove(ContactData.generageNewContactId(contact));
        b.contacts.remove(ContactData.generageNewContactId(contact));

        //Gdx.app.log("END COL", "Fix A: " + "null" + " Collides with Fix B: " + contact.getFixtureB().getUserData().toString());
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
