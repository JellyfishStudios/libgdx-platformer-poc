package com.anogaijin.colour.systems;

import com.anogaijin.colour.components.*;
import com.anogaijin.colour.components.CharacterSensor.CharacterSensorType;
import com.anogaijin.colour.physics.contacts.ContactData;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.attachments.BoundingBoxAttachment;

/**
 * Created by adunne on 2015/09/24.
 */
public class SensorDetectionSystem extends IteratingSystem {
    ComponentMapper<Brain>      bm = ComponentMapper.getFor(Brain.class);
    ComponentMapper<RigidBody>   cm = ComponentMapper.getFor(RigidBody.class);
    ComponentMapper<CharacterSensor>     sm = ComponentMapper.getFor(CharacterSensor.class);

    public SensorDetectionSystem() {
        super(Family.all(Brain.class, RigidBody.class, CharacterSensor.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        RigidBody collider = cm.get(entity);
        CharacterSensor characterSensor = sm.get(entity);
        Brain<TransformationSystem> brain = bm.get(entity);

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
                        if (!characterSensor.entities.containsKey(CharacterSensorType.Bottom))
                            characterSensor.entities.put(CharacterSensorType.Bottom, new Array<Entity>());

                        characterSensor.entities.get(CharacterSensorType.Bottom).add(otherEntity);
                    }

                    break;

                case Top:
                    if (contact.isTouching) {
                        characterSensor.topIsTouching = true;
                        if (!characterSensor.entities.containsKey(CharacterSensorType.Top))
                            characterSensor.entities.put(CharacterSensorType.Top, new Array<Entity>());

                        characterSensor.entities.get(CharacterSensorType.Top).add(otherEntity);
                    }

                    break;

                case Left:
                    if (contact.isTouching) {
                        characterSensor.leftIsTouching = true;
                        if (!characterSensor.entities.containsKey(CharacterSensorType.Left))
                            characterSensor.entities.put(CharacterSensorType.Left, new Array<Entity>());

                        characterSensor.entities.get(CharacterSensorType.Left).add(otherEntity);
                    }

                    break;

                case Right:
                    if (contact.isTouching) {
                        characterSensor.rightIsTouching = true;
                        if (!characterSensor.entities.containsKey(CharacterSensorType.Right))
                            characterSensor.entities.put(CharacterSensorType.Right, new Array<Entity>());

                        characterSensor.entities.get(CharacterSensorType.Right).add(otherEntity);
                    }
                    break;
            }
        }
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
