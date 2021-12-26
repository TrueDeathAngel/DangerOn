package com.company.objects;

import com.company.objects.creatures.Creature;
import com.company.objects.creatures.Mob;
import com.company.objects.items.Item;
import com.company.objects.items.chests.Chest;
import com.company.objects.items.Weapon;
import com.company.objects.items.potions.HealingPotion;
import com.company.objects.items.potions.Potion;
import com.company.objects.items.potions.RegenerationPotion;
import java.util.concurrent.ThreadLocalRandom;

import static com.company.gameplay.GameLogic.floorNumber;
import static com.company.recources.GameResources.*;

public class GameObjectFactory {
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

    public static Potion spawnPotion() {
        int type = ThreadLocalRandom.current().nextInt(2);
        int power = ThreadLocalRandom.current().nextInt((floorNumber + 9) * 2 + 1) + floorNumber + 9;
        int price = ThreadLocalRandom.current().nextInt((int) Math.ceil(power * .3)) + (int) (power * .7);
        if (type == 0)
            return new HealingPotion(
                "Healing Potion",
                price,
                power);
        else {
            power *= 2;
            int duration = ThreadLocalRandom.current().nextInt(11) + 10;
            return new RegenerationPotion(
                "Regeneration Potion",
                price,
                power / duration,
                duration
            );
        }
    }

    public static Chest spawnChest() {
        int size = ThreadLocalRandom.current().nextInt(3);
        Chest chest = new Chest(
                chestNames[size] + " chest",
                (size + 1) * 2,
                chestModels.get(Chest.ChestTypes.REGULAR));
        for (int i = 0; i < (size + 1) * 2; i++) {
            chest.inventory.addItem(spawnRandomItem());
        }
        return chest;
    }

    public static Item spawnRandomItem() {
        int num = ThreadLocalRandom.current().nextInt(2);
        if (num == 0) return spawnWeapon();
        else return spawnPotion();
    }
}
