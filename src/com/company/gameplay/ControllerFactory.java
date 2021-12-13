package com.company.gameplay;

import com.company.objects.GameEntity;
import com.company.objects.creatures.Creature;
import com.company.objects.items.Chest;

public class ControllerFactory {
    public static Controller getSuitableController(GameEntity entity) {
        if (Creature.class.isAssignableFrom(entity.getClass())) return new CreatureController((Creature) entity);
        if (Chest.class.isAssignableFrom(entity.getClass())) return new ChestController((Chest) entity);
        return new Controller() {
            @Override
            public void step() {
                System.out.println("Who the Hell I am?");
            }
        };
    }
}
