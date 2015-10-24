package com.anogaijin.colour.entities;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import java.util.HashMap;

/**
 * Leverages cached components for Add & Remove operations making it possible
 * to remove and add components to your entity without losing a components state
 *
 * This is useful where an entities state is the result of the sum of it's components
 * and is an FSM strategy that works well with the ECS pattern
 *
 * Created by anogaijin on 2015/09/24.
 */
public class CachedEntity extends Entity {
    HashMap<Class<? extends Component>, Component> componentCache = new HashMap<Class<? extends Component>, Component>();

    public Entity restore(Class<? extends Component> componentClass) {
        if (!componentCache.containsKey(componentClass))
            throw new IllegalArgumentException("Requested component not found.");

        return super.add(componentCache.get(componentClass));
    }

    @Override
    public Entity add(Component component) {
        componentCache.put(component.getClass(), component);

        return super.add(component);
    }
}
