package com.anogaijin.colour.systems;

import com.anogaijin.colour.components.*;
import com.anogaijin.colour.components.CharacterSensor.CharacterSensorType;
import com.anogaijin.colour.physics.contacts.ContactData;
import com.anogaijin.colour.systems.states.PlayerState;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.esotericsoftware.spine.attachments.BoundingBoxAttachment;

/**
 * Created by adunne on 2015/09/24.
 */
public class CharacterInteractionSystem extends IteratingSystem {
    ComponentMapper<Brain>      bm = ComponentMapper.getFor(Brain.class);
    ComponentMapper<RigidBody>   cm = ComponentMapper.getFor(RigidBody.class);
    ComponentMapper<CharacterSensor>     sm = ComponentMapper.getFor(CharacterSensor.class);

    public CharacterInteractionSystem() {
        super(Family.all(Brain.class, RigidBody.class, CharacterSensor.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        RigidBody collider = cm.get(entity);
        CharacterSensor characterSensor = sm.get(entity);
        Brain<TransformationSystem> brain = bm.get(entity);

        // Inform the brain!
        //
        processInteraction(entity, collider, characterSensor, brain, deltaTime);
    }

    private void processInteraction(Entity entity, RigidBody collider, CharacterSensor characterSensor, Brain brain, float deltaTime) {
        resetCharacterSensor(characterSensor);

        for (ContactData contact : collider.contacts.values()) {
            // We obviously need information about the entity we're about to (or not) interact with
            //
            Entity otherEntity = getOtherEntity(entity, contact);

            // We also need fixtures to figure out what kind of interaction this is going to be (if any)
            //
            Fixture myFixture = getMyFixture(entity, contact);

            // If it's not one of my interaction sensors then nothing more to see here
            //
            if (!myFixture.isSensor())
                continue;

            switch (getSensorType(myFixture)) {
                case Bottom:
                    if (contact.isTouching) {
                        characterSensor.bottomIsTouching = true;
                        characterSensor.entities.put(CharacterSensorType.Bottom, otherEntity);
                    }

                    break;

                case Top:
                    if (contact.isTouching) {
                        characterSensor.topIsTouching = true;
                        characterSensor.entities.put(CharacterSensorType.Top, otherEntity);
                    }

                    break;

                case Left:
                    if (contact.isTouching) {
                        characterSensor.leftIsTouching = true;
                        characterSensor.entities.put(CharacterSensorType.Left, otherEntity);
                    }

                    break;

                case Right:
                    if (contact.isTouching) {
                        characterSensor.rightIsTouching = true;
                        characterSensor.entities.put(CharacterSensorType.Right, otherEntity);
                    }
                    break;
            }
        }

        // We only handle the most basic and generic of interactions: grounded or in air
        //
        // Any bespoke interactions available to a character should be handled by
        // bespoke interaction systems based on the sensor data we have provided.
        //
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
        sensor.entities.clear();
    }

    private CharacterSensorType getSensorType(Fixture myFixture) {
        if (myFixture.getUserData().getClass() == BoundingBoxAttachment.class) {
            BoundingBoxAttachment attachment = (BoundingBoxAttachment)myFixture.getUserData();

            switch (attachment.getName()) {
                case "top_interaction_sensor":
                    return CharacterSensorType.Top;

                case "bottom_interaction_sensor":
                    return CharacterSensorType.Bottom;

                case "left_interaction_sensor":
                    return CharacterSensorType.Left;

                case "right_interaction_sensor":
                    return CharacterSensorType.Right;
            }
        }

        return CharacterSensorType.Unknown;
    }

    private Entity getOtherEntity(Entity me, ContactData contact) {
        if (contact.fixtureA.getBody().getUserData() == me)
            return (Entity)contact.fixtureB.getBody().getUserData();

        return (Entity)contact.fixtureA.getBody().getUserData();
    }

    private Fixture getMyFixture(Entity me, ContactData contact) {
        if (contact.fixtureA.getBody().getUserData() == me)
            return contact.fixtureA;

        return contact.fixtureB;
    }
}
