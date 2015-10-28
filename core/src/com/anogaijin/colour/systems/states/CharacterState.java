package com.anogaijin.colour.systems.states;

import com.anogaijin.colour.components.*;
import com.anogaijin.colour.entities.CachedEntity;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;

/**
 * Created by adunne on 2015/09/20.
 */
public enum CharacterState implements State<CachedEntity> {
    Idle() {
        @Override
        public void enter(CachedEntity entity) {
            entity.restore(Walk.class);
            entity.restore(Jump.class);

            Util.trySetAnimationComponent(entity, 0, "Idle", true);
        }

        @Override
        public void update(CachedEntity entity) {
        }

        @Override
        public void exit(CachedEntity entity) {
        }

        @Override
        public boolean onMessage(CachedEntity entity, Telegram telegram) {
            return false;
        }
    },

    Walking() {
        @Override
        public void enter(CachedEntity entity) {
            entity.restore(Jump.class);

            Util.trySetAnimationComponent(entity, 0, "Walk", true);
        }

        @Override
        public void update(CachedEntity entity) {
        }

        @Override
        public void exit(CachedEntity entity) {
        }

        @Override
        public boolean onMessage(CachedEntity entity, Telegram telegram) {
            return false;
        }
    },

    Jumping() {
        @Override
        public void enter(CachedEntity entity) {
            entity.remove(Walk.class);

            Util.trySetAnimationComponent(entity, 0, "Jump", true);
        }

        @Override
        public void update(CachedEntity entity) {
        }

        @Override
        public void exit(CachedEntity entity) {
            entity.restore(Walk.class);
            entity.restore(Jump.class);
        }

        @Override
        public boolean onMessage(CachedEntity entity, Telegram telegram) {
            return false;
        }
    },

    Falling() {
        @Override
        public void enter(CachedEntity entity) {
            entity.remove(Walk.class);
            entity.remove(Jump.class);

            Util.trySetAnimationComponent(entity, 0, "Idle", true);
        }

        @Override
        public void update(CachedEntity entity) {
        }

        @Override
        public void exit(CachedEntity entity) {
            entity.restore(Walk.class);
            entity.restore(Jump.class);
        }

        @Override
        public boolean onMessage(CachedEntity entity, Telegram telegram) {
            return false;
        }
    };

    private static class Util {
        public static boolean trySetAnimationComponent(Entity entity, int track, String animation, boolean loop) {
            ComponentMapper<Animation> am = ComponentMapper.getFor(Animation.class);
            Animation animationComponent = am.get(entity);
            if (animationComponent != null) {
                animationComponent.setState(track, animation, loop);
                return true;
            }

            return false;
        }
    }
}
