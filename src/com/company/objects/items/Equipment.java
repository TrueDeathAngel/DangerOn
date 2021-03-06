package com.company.objects.items;

public class Equipment extends Item {
    private final int defencePoints;
    private final EquipmentType equipmentType;

    public Equipment(String name, int price, int defencePoints, EquipmentType equipmentType) {
        super(name, price);
        this.equipmentType = equipmentType;
        this.defencePoints = defencePoints;
    }

    public int getDefencePoints() { return defencePoints; }

    public EquipmentType getEquipmentType() { return equipmentType; }

    public enum EquipmentType {
        HELMET,
        BREASTPLATE,
        FOREARMS,
        PANTS,
        GLOVES,
        BOOTS
    }
}
