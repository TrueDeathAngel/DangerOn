package com.company;

import com.company.creatures.Creature;
import com.company.creatures.Mob;
import com.company.items.Weapon;

import java.util.concurrent.ThreadLocalRandom;

import static com.company.GameLogic.floorNumber;

public class GameObjectFactory
{
    public static final String[] mobNames = {"Zombie", "Ogre", "Skeleton", "Cyclops", "Goblin", "Centaurs", "Fauns", "Minotaur", "Demon", "Imp"};
    static String[] weaponNames = {"Sword", "Axe", "Pickaxe", "Bow", "Crossbow", "Spear", "Dagger", "Mace", "Hammer", "Sickle"};

    public static Creature spawnCreature() {
        return new Creature
                (
                        mobNames[ThreadLocalRandom.current().nextInt(mobNames.length)],
                        ThreadLocalRandom.current().nextInt(14 + floorNumber) + floorNumber,
                        ThreadLocalRandom.current().nextInt(4 + floorNumber) + floorNumber,
                        ThreadLocalRandom.current().nextInt(14 + floorNumber) + floorNumber
                );
    }

    public static Mob spawnMob() {
        return new Mob
                (
                        mobNames[ThreadLocalRandom.current().nextInt(mobNames.length)],
                        ThreadLocalRandom.current().nextInt(14 + floorNumber) + floorNumber,
                        ThreadLocalRandom.current().nextInt(4 + floorNumber) + floorNumber,
                        ThreadLocalRandom.current().nextInt(14 + floorNumber) + floorNumber,
                        spawnWeapon()
                );
    }

    public static Weapon spawnWeapon() {
        return new Weapon
                (
                        weaponNames[ThreadLocalRandom.current().nextInt(weaponNames.length)],
                        ThreadLocalRandom.current().nextInt(5 + floorNumber) + floorNumber,
                        ThreadLocalRandom.current().nextInt(25 + floorNumber) + 10 + floorNumber,
                        ThreadLocalRandom.current().nextInt(5 + floorNumber) + floorNumber
                );
    }
}
