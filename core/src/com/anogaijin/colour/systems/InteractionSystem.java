package com.anogaijin.colour.systems;

import com.anogaijin.colour.components.*;
import com.anogaijin.colour.physics.contacts.ContactData;
import com.anogaijin.colour.systems.states.PlayerState;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.esotericsoftware.spine.attachments.BoundingBoxAttachment;

/**
 * Created by adunne on 2015/09/24.
 */
public class InteractionSystem extends IteratingSystem {
    ComponentMapper<Brain>      bm = ComponentMapper.getFor(Brain.class);
    ComponentMapper<Collider>   cm = ComponentMapper.getFor(Collider.class);
    ComponentMapper<CharacterSensor>     sm = ComponentMapper.getFor(CharacterSensor.class);

    public InteractionSystem() {
        super(Family.all(Brain.class, Collider.class, CharacterSensor.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Collider collider = cm.get(entity);
        CharacterSensor characterSensor = sm.get(entity);
        Brain<TransformationSystem> brain = bm.get(entity);

        // Inform the brain!
        //
        processInteraction(entity, collider, characterSensor, brain, deltaTime);
    }

    private void processInteraction(Entity entity, Collider collider, CharacterSensor characterSensor, Brain brain, float deltaTime) {
        resetCharacterSensor(characterSensor);

        for (ContactData contact : collider.contacts.values()) {
            //Gdx.app.log("COL", "Fix A: " +  "null" + " Collides with Fix B: " + contact.fixtureB.getUserData().toString());

            // We obviously need information about the entity we're about to (or not) interact with
            //
            Entity herEntity = getHerEntity(entity, contact);

            // We also need fixtures to figure out what kind of interaction this is going to be (if any)
            //
            Fixture herFixture = getHerFixture(entity, contact);
            Fixture myFixture = getMyFixture(entity, contact);

            // If it's not one of my interaction sensors then nothing more to see here
            //
            if (!myFixture.isSensor())
                continue;

            switch (getSensorType(myFixture)) {
                case Bottom:
                    if (contact.isTouching)
                        characterSensor.bottomIsTouching = true;

                    break;

                case Top:
                    if (contact.isTouching)
                        characterSensor.topIsTouching = true;

                    break;

                case Left:
                    if (contact.isTouching)
                        characterSensor.leftIsTouching = true;

                    break;

                case Right:
                    if (contact.isTouching)
                        characterSensor.rightIsTouching = true;

                    break;
            }
        }

        if (characterSensor.bottomIsTouching)
            brain.movement.changeState(PlayerState.Grounded);
        else
            brain.movement.changeState(PlayerState.Airborn);
    }

    private void resetCharacterSensor(CharacterSensor sensor) {
        sensor.topIsTouching = false;
        sensor.bottomIsTouching = false;
        sensor.leftIsTouching = false;
        sensor.rightIsTouching = false;
    }

    private InteractionSensorType getSensorType(Fixture myFixture) {
        if (myFixture.getUserData().getClass() == BoundingBoxAttachment.class) {
            BoundingBoxAttachment attachment = (BoundingBoxAttachment)myFixture.getUserData();

            switch (attachment.getName()) {
                case "top_interaction_sensor":
                    return InteractionSensorType.Top;

                case "bottom_interaction_sensor":
                    return InteractionSensorType.Bottom;

                case "left_interaction_sensor":
                    return InteractionSensorType.Left;

                case "right_interaction_sensor":
                    return InteractionSensorType.Right;
            }
        }

        return InteractionSensorType.Unknown;
    }

    private Entity getHerEntity(Entity me, ContactData contact) {
        if (contact.fixtureA.getBody().getUserData() == me)
            return (Entity)contact.fixtureB.getBody().getUserData();

        return (Entity)contact.fixtureA.getBody().getUserData();
    }

    private Fixture getHerFixture(Entity me, ContactData contact) {
        if (contact.fixtureA.getBody().getUserData() == me)
            return contact.fixtureB;

        return contact.fixtureA;
    }

    private Fixture getMyFixture(Entity me, ContactData contact) {
        if (contact.fixtureA.getBody().getUserData() == me)
            return contact.fixtureA;

        return contact.fixtureB;
    }

    public enum InteractionSensorType {
        Top,
        Bottom,
        Left,
        Right,
        Unknown;
    }
}
