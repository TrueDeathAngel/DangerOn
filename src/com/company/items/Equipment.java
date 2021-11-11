package com.company.items;

public class Equipment extends Item
{
    private int defencePoints;
    private EquipmentType equipmentType;

    public Equipment(String name, int price, int defencePoints, EquipmentType equipmentType)
    {
        super(name, price);
        this.equipmentType = equipmentType;
        this.defencePoints = defencePoints;
    }

    public int getDefencePoints() { return defencePoints; }

    public EquipmentType getEquipmentType() { return equipmentType; }

    public enum EquipmentType
    {
        HELMET,
        BREASTPLATE,
        FOREARMS,
        PANTS,
        GLOVES,
        BOOTS
    }
}
