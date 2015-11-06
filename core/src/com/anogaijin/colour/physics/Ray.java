package com.anogaijin.colour.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by adunne on 2015/11/06.
 */
public class Ray implements RayCastCallback {
    public Fixture lastFixtureHit;
    public boolean hasHit;
    public short filter;

    @Override
    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
        if ((fixture.getFilterData().categoryBits & filter) != 0) {
            hasHit = true;
            lastFixtureHit = fixture;

            return 0;
        }

        return 1;
    }

    public boolean cast (World world, Vector2 start, Vector2 end, short filter) {
        this.filter = filter;
        this.hasHit = false;

        // Wait for the callback.
        //
        // Will be called for each fixture hit, but we're only interested in the first.
        //
        world.rayCast(this, start, end);

        return hasHit;
    }
}
