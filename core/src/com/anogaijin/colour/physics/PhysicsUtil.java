package com.anogaijin.colour.physics;

/**
 * Created by adunne on 2015/10/23.
 */
public class PhysicsUtil {
    public static float calculateRequiredForce(float mass, float desiredVelocity, float actualVelocity, float timeStep) {
        // Reset force if there is no desired movement otherwise the force will be countering gravity if on the y axis
        //
        if (desiredVelocity == 0f)
            return 0f;

        // f = mv/t
        //
        return mass * (desiredVelocity - actualVelocity) / timeStep;
    }

    public static float calculateRequiredImpulse(float mass, float desiredVelocity, float actualVelocity) {
        // Reset force if there is no desired movement otherwise the force will be countering gravity if on the y axis
        //
        if (desiredVelocity == 0f)
            return 0f;

        // f = mv/t
        //
        return mass * (desiredVelocity - actualVelocity);
    }
}
