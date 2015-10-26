package com.anogaijin.colour.systems.states;

import com.anogaijin.colour.components.*;
import com.anogaijin.colour.entities.CachedEntity;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;

/**
 * Created by adunne on 2015/09/20.
 */
public enum PlayerState implements State<CachedEntity> {
    Grounded() {
        @Override
        public void enter(CachedEntity entity) {

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

    Airborn() {
        @Override
        public void enter(CachedEntity entity) {
            entity.remove(Jump.class);
        }

        @Override
        public void update(CachedEntity entity) {

        }

        @Override
        public void exit(CachedEntity entity) {
            entity.restore(Jump.class);
        }

        @Override
        public boolean onMessage(CachedEntity entity, Telegram telegram) {
            return false;
        }
    };
}
