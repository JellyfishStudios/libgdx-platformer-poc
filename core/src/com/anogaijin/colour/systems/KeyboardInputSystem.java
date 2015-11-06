package com.anogaijin.colour.systems;

import com.anogaijin.colour.components.Input;
import com.anogaijin.colour.components.KeyboardController;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;

/**
 * Created by adunne on 2015/10/28.
 */
public class KeyboardInputSystem extends IteratingSystem {
    ComponentMapper<KeyboardController> km = ComponentMapper.getFor(KeyboardController.class);
    ComponentMapper<Input> im = ComponentMapper.getFor(Input.class);

    public KeyboardInputSystem() {
        super(Family.all(Input.class, KeyboardController.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        KeyboardController controller = km.get(entity);
        Input input = im.get(entity);

        resetInputs(input);

        if (Gdx.input.isKeyPressed(controller.RIGHT))
            input.MOVE_RIGHT = true;
        else if (Gdx.input.isKeyPressed(controller.LEFT))
            input.MOVE_LEFT = true;

        if (Gdx.input.isKeyJustPressed(controller.UP))
            input.JUMP = true;
        else if (Gdx.input.isKeyJustPressed(controller.DOWN))
            input.DROP = true;
    }

    private void resetInputs(Input input) {
        input.MOVE_LEFT = false;
        input.MOVE_RIGHT = false;
        input.JUMP = false;
        input.DROP = false;
    }
}
