package com.anogaijin.colour.physics;

import com.anogaijin.colour.physics.contacts.ContactData;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
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

    public static boolean isMySensorTouching(String sensorName, Array<ContactData> contacts) {
        if (getMyTouchingSensorContact(sensorName, contacts) != null)
            return true;

        return false;
    }

    public static ContactData getMyTouchingSensorContact(String sensorName, Array<ContactData> contacts) {
        for (ContactData contact : contacts) {
            if (contact.myFixture.isSensor()) {
                BoundingBoxAttachment attachment = (BoundingBoxAttachment)contact.myFixture.getUserData();

                if (attachment.getName().equals(sensorName) && contact.isTouching)
                    return contact;
            }
        }

        return null;
    }
}
