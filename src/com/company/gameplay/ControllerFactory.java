package com.company.gameplay;

import com.company.objects.GameEntity;
import com.company.objects.creatures.Creature;
import com.company.objects.creatures.Mob;
import com.company.objects.items.chests.Chest;

public class ControllerFactory {
    public static Controller getSuitableController(GameEntity entity) {
        if (entity instanceof Creature creature) return new CreatureController(creature);
        if (entity instanceof Chest chest) return new ChestController(chest);
        return new Controller() {
            @Override
            public void step() {
                System.out.println("Who the Hell I am?");
            }
        };
    }
}
