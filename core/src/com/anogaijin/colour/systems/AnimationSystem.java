package com.anogaijin.colour.systems;

import com.anogaijin.colour.components.Model;
import com.anogaijin.colour.components.Animation;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;

/**
 * Created by adunne on 2015/10/27.
 */
public class AnimationSystem extends IteratingSystem {
    ComponentMapper<Animation> am = ComponentMapper.getFor(Animation.class);
    ComponentMapper<Model> mm = ComponentMapper.getFor(Model.class);

    public AnimationSystem() {
        super(Family.all(Animation.class, Model.class).get());
    }
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Animation animation = am.get(entity);

        animation.state.update(Gdx.graphics.getDeltaTime());
        animation.state.apply(mm.get(entity).skeleton);
    }
}
