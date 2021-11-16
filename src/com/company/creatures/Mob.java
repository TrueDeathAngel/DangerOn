package com.company.creatures;

import com.company.items.Equipment;
import com.company.items.Weapon;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class Mob extends Creature
{
    private HashMap<Equipment.EquipmentType, Equipment> equipmentItems = new HashMap();
    private Weapon weapon;

    public Mob(String name, int hitPoints, int attackPower, int defencePoints)
    {
        super(name, hitPoints, attackPower, defencePoints);
        model = name.charAt(0);
    }

    public Mob(String name, int hitPoints, int attackPower, int defencePoints, Weapon weapon)
    {
        this(name, hitPoints, attackPower, defencePoints);
        setWeapon(weapon);
    }

    @Override
    public int getDamage()
    {
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
    public int getDefencePoints()
    {
        int defence = super.getDefencePoints();
        for(Equipment equipment : equipmentItems.values())
            defence += equipment.getDefencePoints();
        return defence;
    }

    public int getWeaponAttackPower() { return weapon.getAttackPower(); }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    public void addEquipment(Equipment equipment)
    {
        if(!equipmentItems.containsKey(equipment.getEquipmentType()) ||
                equipmentItems.get(equipment.getEquipmentType()).getDefencePoints() <= equipment.getDefencePoints())
            equipmentItems.put(equipment.getEquipmentType(), equipment);
    }

    public HashMap<Equipment.EquipmentType, Equipment> getEquipmentItems()
    {
        return equipmentItems;
    }
}
