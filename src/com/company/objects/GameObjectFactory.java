package com.company.objects;

import com.company.objects.creatures.Creature;
import com.company.objects.creatures.Mob;
import com.company.objects.items.Chest;
import com.company.objects.items.Weapon;

import java.util.concurrent.ThreadLocalRandom;

import static com.company.gameplay.GameLogic.floorNumber;

public class GameObjectFactory
{
    public static final String[] mobNames = {"Zombie", "Ogre", "Skeleton", "Cyclops", "Goblin", "Centaurs", "Fauns", "Minotaur", "Demon", "Imp"};
    static String[] weaponNames = {"Sword", "Axe", "Pickaxe", "Bow", "Crossbow", "Spear", "Dagger", "Mace", "Hammer", "Sickle"};
    static String[] chestNames = {"Small", "Regular", "Big"};

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

    public static Chest spawnChest() {
        int size = ThreadLocalRandom.current().nextInt(3);
        Chest chest = new Chest(chestNames[size] + " chest", (size + 1) * 4);
        for (int i = 0; i < (size + 1) * 4; i++) {
            chest.inventory.addItem(spawnWeapon());
        }
        return chest;
    }
}
