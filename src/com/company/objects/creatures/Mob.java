package com.company.objects.creatures;

import com.company.objects.items.Equipment;
import com.company.objects.items.Item;
import com.company.objects.items.Weapon;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import static com.company.gameplay.GameLogic.*;

public class Mob extends Creature {
    public final HashMap<Equipment.EquipmentType, Equipment> equipmentItems = new HashMap();
    public Weapon weapon;

    public Mob(String name, int maxHitPoints, int attackPower, int defencePoints) {
        super(name, maxHitPoints, attackPower, defencePoints);
        model = new TextCharacter(name.charAt(0), TextColor.ANSI.RED, TextColor.ANSI.DEFAULT);
    }

    public Mob(String name, int maxHitPoints, int attackPower, int defencePoints, Weapon weapon) {
        this(name, maxHitPoints, attackPower, defencePoints);
        setWeapon(weapon);
    }

    @Override
    public int getCost() {
        return super.getCost() + getWeaponAttackPower() + getDefencePoints();
    }

    @Override
    public int getDamage() {
        int damage = getAttackPower();
        if(weapon != null) damage += ThreadLocalRandom.current().nextInt(weapon.getAttackPower());
        return damage;
    }

    /*@Override
    public void receiveDamage(short damage)
    {
        if(damage > defencePoints) hitPoints -= damage - defencePoints;
        else hitPoints--;
    }*/

    @Override
    public ArrayList<Item> getLoot() {
        ArrayList<Item> items = super.getLoot();
        items.addAll(equipmentItems.values());
        items.add(weapon);
        return items;
    }

    @Override
    public int getDefencePoints()
    {
        return super.getDefencePoints() + equipmentItems.values().stream().mapToInt(Equipment::getDefencePoints).sum();
    }

    public int getWeaponAttackPower() { return weapon != null ? weapon.getAttackPower() : 0; }

    public void setWeapon(Weapon weapon) {
        if (this.weapon != null) inventory.addItem(this.weapon);
        this.weapon = weapon;
    }

    public HashMap<Equipment.EquipmentType, Equipment> getEquipmentItems()
    {
        return equipmentItems;
    }

    public void equip(Item item) {
        inventory.removeByIndex(inventoryCursorPosition);
        Equipment.EquipmentType type = ((Equipment) item).getEquipmentType();
        if (equipmentItems.containsKey(type)) {
            inventory.addItem(hero.equipmentItems.get(type));
        }
        hero.equipmentItems.put(type, (Equipment) item);
    }
}
