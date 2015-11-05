package com.anogaijin.colour.physics;

import com.anogaijin.colour.physics.contacts.ContactData;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.attachments.BoundingBoxAttachment;

/**
 * Created by adunne on 2015/10/23.
 */
public class PhysicsUtil {
    public static String TOP_SENSOR = "top_interaction_sensor";
    public static String BOTTOM_SENSOR = "bottom_interaction_sensor";
    public static String BACK_SENSOR = "back_interaction_sensor";
    public static String FRONT_SENSOR = "front_interaction_sensor";
    public static String UPPER_BACK_SENSOR = "upper_back_interaction_sensor";
    public static String UPPER_FRONT_SENSOR = "upper_front_interaction_sensor";

    public static float calculateRequiredForce(float mass, float desiredVelocity, float actualVelocity, float timeStep) {
        // f = mv/t
        //
        return mass * (desiredVelocity - actualVelocity) / timeStep;
    }

    public static float calculateRequiredImpulse(float mass, float desiredVelocity, float actualVelocity) {
        // f = mv/t
        //
        return mass * (desiredVelocity - actualVelocity);
    }

    public static boolean isSensorContacting(Entity me, String sensorName, Array<ContactData> contacts) {
        if (getContactingSensor(me, sensorName, contacts) != null)
            return true;

        return false;
    }

    public static Fixture getContactingSensor(Entity me, String sensorName, Array<ContactData> contacts) {
        for (ContactData contact : contacts) {
            Fixture fixture = getFixture(me, contact);

            if (fixture.isSensor()) {
                BoundingBoxAttachment attachment = (BoundingBoxAttachment)fixture.getUserData();

                if (attachment.getName().equals(sensorName) && contact.isTouching)
                    return fixture;
            }
        }

        return null;
    }

    public static Fixture getFixture(Entity me, ContactData contact) {
        if (contact.fixtureA.getBody().getUserData() == me)
            return contact.fixtureA;

        return contact.fixtureB;
    }
}
