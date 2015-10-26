package com.anogaijin.colour.physics;

/**
 * Created by adunne on 2015/10/23.
 */
public class PhysicsUtil {
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
}
