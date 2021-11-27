package com.company.objects.items;

public class Weapon extends Item
{
    private int requiredLevel = 1;
    private int attackPower = 1;
    private int attackRange = 1;

    public Weapon(String name, int attackPower, int price, int requiredLevel)
    {
        super(name, price);
        this.attackPower = attackPower;
        this.requiredLevel = requiredLevel;
    }

    public int getRequiredLevel()
    {
        return requiredLevel;
    }

    public int getAttackPower() { return attackPower; }
}
