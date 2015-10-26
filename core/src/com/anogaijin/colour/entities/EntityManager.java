package com.anogaijin.colour.entities;

import com.anogaijin.colour.assets.AssetManagerEx;
import com.anogaijin.colour.components.*;
import com.anogaijin.colour.components.Transform;
import com.anogaijin.colour.systems.*;
import com.anogaijin.colour.systems.states.PlayerState;
import com.anogaijin.rubeloaderlite.RubeScene;
import com.anogaijin.rubeloaderlite.containers.RubeRigidBody;
import com.anogaijin.rubeloaderlite.containers.RubeTexture;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ObjectMap;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.BoundingBoxAttachment;

/**
 * Created by adunne on 2015/10/23.
 */
public class EntityManager {
    Engine ecsEngine;
    World physicsWorld;
    AssetManagerEx assetManager;

    float humanDensity = 50f;

    short CATEGORY_PLAYER = 0x01;
    short CATEGORY_OBJECTS = 0x02;
    short CATEGORY_ANYTHING = 0xFF;

    InteractionSystem interactionSystem;
    MovementSystem movementSystem;
    JumpSystem jumpSystem;
    TransformationSystem transformationSystemSystem;
    CameraSystem cameraSystem;
    RenderingSystem modelRenderingSystem;

    public EntityManager(Engine ecsEngine, AssetManagerEx assetManager) {
        this.ecsEngine = ecsEngine;
        this.assetManager = assetManager;
    }

    public void initialiseSystems() {
        ecsEngine.addSystem(movementSystem = new MovementSystem());
        ecsEngine.addSystem(jumpSystem = new JumpSystem());
        ecsEngine.addSystem(interactionSystem = new InteractionSystem());
        ecsEngine.addSystem(transformationSystemSystem = new TransformationSystem());
        ecsEngine.addSystem(cameraSystem = new CameraSystem());
        ecsEngine.addSystem(modelRenderingSystem = new RenderingSystem());
    }

    private void refreshSystems() {
        movementSystem.addedToEngine(ecsEngine);
        jumpSystem.addedToEngine(ecsEngine);
        interactionSystem.addedToEngine(ecsEngine);
        transformationSystemSystem.addedToEngine(ecsEngine);
        cameraSystem.addedToEngine(ecsEngine);
        modelRenderingSystem.addedToEngine(ecsEngine);
    }

    public void initialiseSceneEntities() {
        RubeScene scene = assetManager.get("testLevel");
        physicsWorld = scene.getWorld();

        ObjectMap<Integer, CachedEntity> entities = new ObjectMap<>();
        for (RubeRigidBody rubeRigidBody : scene.getAllRigidBodies()) {
            CachedEntity entity = new CachedEntity();

            rubeRigidBody.rigidBody.setUserData(entity);

            entity.add(new Collider(rubeRigidBody.rigidBody));
            ecsEngine.addEntity(entity);

            entities.put(rubeRigidBody.rigidBody.hashCode(), entity);
        }

        for (RubeTexture rubeTexture : scene.getAllTextures()) {
            CachedEntity entity;

            if (rubeTexture.rubeRigidBody != null) {
                if (entities.containsKey(rubeTexture.rubeRigidBody.rigidBody.hashCode())) {
                    entity = entities.get(rubeTexture.rubeRigidBody.rigidBody.hashCode());
                }
                else {
                    entity = new CachedEntity();

                    rubeTexture.rubeRigidBody.rigidBody.setUserData(entity);
                    entity.add(new Collider(rubeTexture.rubeRigidBody.rigidBody));
                    ecsEngine.addEntity(entity);
                }
            }
            else {
                entity = new CachedEntity();

                rubeTexture.rubeRigidBody.rigidBody.setUserData(entity);
                ecsEngine.addEntity(entity);
            }

            entity.add(new Image(
                    rubeTexture.texture,
                    rubeTexture.width,
                    rubeTexture.height,
                    rubeTexture.color,
                    rubeTexture.filter,
                    rubeTexture.opacity));
            entity.add(new Transform(
                    new Vector2(
                            rubeTexture.rubeRigidBody.rigidBody.getPosition().x - rubeTexture.width/2,
                            rubeTexture.rubeRigidBody.rigidBody.getPosition().y - rubeTexture.height/2),
                    new Vector2(1f, 1f),
                    rubeTexture.rubeRigidBody.rigidBody.getAngle() * MathUtils.radDeg));
        }

        refreshSystems();
    }

    public void initialiseCharactersEntities() {
        try {
            CachedEntity charEntity = new CachedEntity();

            Transform trans = new Transform(new Vector2(2f, 5f), new Vector2(1f, 1f), 0f);

            Skeleton skeleton = new Skeleton(assetManager.get("heroModel", SkeletonData.class));
            skeleton.setToSetupPose();
            skeleton.setPosition(trans.position.x, trans.position.y);
            skeleton.getRootBone().setRotation(trans.rotation);
            skeleton.updateWorldTransform();

            Body body = loadRigidBody(skeleton);
            body.setUserData(charEntity);

            charEntity.add(trans);
            charEntity.add(new Model(skeleton));
            charEntity.add(new Collider(body));
            charEntity.add(new CharacterSensor());
            charEntity.add(new Motion());
            charEntity.add(new Jump());
            charEntity.add(new Controller());
            charEntity.add(new Brain<>(charEntity, PlayerState.Grounded));

            ecsEngine.addEntity(charEntity);

            // good time to add a camera and attach it to this
            //
            CachedEntity camEntity = new CachedEntity();
            camEntity.add(new Camera(ecsEngine.getSystem(RenderingSystem.class).getCamera(), charEntity));

            ecsEngine.addEntity(camEntity);

        }catch (Exception ex) {
        }

        refreshSystems();
    }

    public World getPhysicsWorld() {
        return physicsWorld;
    }

    private Body loadRigidBody(Skeleton model) throws Exception {
        Body body = createModelBody(model);

        for (Slot slot : model.getSlots()) {
            if (slot.getAttachment() != null && slot.getAttachment().getClass() == BoundingBoxAttachment.class) {
                BoundingBoxAttachment attachment = (BoundingBoxAttachment)slot.getAttachment();

                if (attachment.getName().endsWith("sensor"))
                    // Type of sensor based on it's name
                    addSensor(body, attachment);

                else if (attachment.getName().endsWith("capsule"))
                    // We only expect one of these for obvious reasons
                    addCapsule(body, attachment);
            }
        }

        return body;
    }

    private void addCapsule(Body body, BoundingBoxAttachment attachment) throws Exception {
        PolygonShape shape = new PolygonShape();
        shape.set(attachment.getVertices());

        // We're expecting the artist is working with a 4 point polygon (Spine does not support primitives)
        //
        if (shape.getVertexCount() != 4)
            throw new Exception("Unsupported capsule collider: only rectangles supported.");

        Vector2 pointA = new Vector2();
        Vector2 pointB = new Vector2();
        Vector2 pointC = new Vector2();

        shape.getVertex(0, pointA);
        shape.getVertex(1, pointB);
        shape.getVertex(2, pointC);

        // TODO: Relies on points drawn from the rightIsTouching counter-clockwise (gotta fix this!)
        //
        float height = (float)Math.sqrt(Math.pow(pointB.x-pointA.x, 2) + Math.pow(pointB.y-pointA.y, 2));
        float width = (float)Math.sqrt(Math.pow(pointC.x-pointB.x, 2) + Math.pow(pointC.y-pointB.y, 2));

        float radius = width / 2;
        float diameter = width;

        PolygonShape rectangle = new PolygonShape();
        rectangle.setAsBox(
                width/2,
                (height-diameter)/2,
                new Vector2(body.getLocalCenter().x, body.getLocalCenter().y + height/2),
                0f);

        CircleShape circleTop = new CircleShape();
        circleTop.setRadius(radius);
        circleTop.setPosition(new Vector2(body.getLocalCenter().x, body.getLocalCenter().y + height-radius));

        CircleShape circleBottom = new CircleShape();
        circleBottom.setRadius(radius);
        circleBottom.setPosition(new Vector2(body.getLocalCenter().x, body.getLocalCenter().y + radius));

        FixtureDef squareFixtureDef = new FixtureDef();
        squareFixtureDef.shape = rectangle;
        squareFixtureDef.density = humanDensity * 0.50f;
        squareFixtureDef.friction = 0f;
        squareFixtureDef.filter.categoryBits = CATEGORY_PLAYER;
        squareFixtureDef.filter.maskBits = CATEGORY_OBJECTS;

        FixtureDef circularTopFixtureDef = new FixtureDef();
        circularTopFixtureDef.shape = circleTop;
        circularTopFixtureDef.density = humanDensity * 0.25f;
        circularTopFixtureDef.friction =0f;
        circularTopFixtureDef.filter.categoryBits = CATEGORY_PLAYER;
        circularTopFixtureDef.filter.maskBits = CATEGORY_OBJECTS;

        FixtureDef circularBottomFixtureDef = new FixtureDef();
        circularBottomFixtureDef.shape = circleBottom;
        circularBottomFixtureDef.density = humanDensity * 0.25f;
        circularBottomFixtureDef.friction = 0f;
        circularBottomFixtureDef.filter.categoryBits = CATEGORY_PLAYER;
        circularBottomFixtureDef.filter.maskBits = CATEGORY_OBJECTS;

        body.createFixture(squareFixtureDef).setUserData(attachment);
        body.createFixture(circularTopFixtureDef).setUserData(attachment);
        body.createFixture(circularBottomFixtureDef).setUserData(attachment);

        shape.dispose();
        circleTop.dispose();
        circleBottom.dispose();
    }

    private void addSensor(Body body, BoundingBoxAttachment attachment) {
        PolygonShape shape = new PolygonShape();
        shape.set(attachment.getVertices());

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef).setUserData(attachment);

        shape.dispose();
    }

    private Body createModelBody(Skeleton model) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation = true;
        bodyDef.position.set(
                model.getX() + model.getRootBone().getWorldX(),
                model.getY() + model.getRootBone().getWorldY());
        bodyDef.angle = (model.getRootBone().getWorldRotation() * MathUtils.degRad);

        return physicsWorld.createBody(bodyDef);
    }
}
