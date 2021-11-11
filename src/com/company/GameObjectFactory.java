package com.company;

import com.company.creature.Creature;
import com.company.creature.Mob;

import java.util.concurrent.ThreadLocalRandom;

public class GameObjectFactory
{
    public static final String[] mobNames = {"Zombie", "Ogre", "Skeleton", "Cyclops", "Goblin", "Centaurs", "Fauns", "Minotaur", "Demon", "Imp"};

    public static Creature spawnCreature()
    {
        return new Creature
                (
                        mobNames[ThreadLocalRandom.current().nextInt(mobNames.length)],
                        ThreadLocalRandom.current().nextInt(14) + 1,
                        ThreadLocalRandom.current().nextInt(4) + 1,
                        ThreadLocalRandom.current().nextInt(14) + 1
                );
    }

    public static Mob spawnMob()
    {
        return new Mob
                (
                        mobNames[ThreadLocalRandom.current().nextInt(mobNames.length)],
                        ThreadLocalRandom.current().nextInt(14) + 1,
                        ThreadLocalRandom.current().nextInt(4) + 1,
                        ThreadLocalRandom.current().nextInt(14) + 1
                );
    }
}
