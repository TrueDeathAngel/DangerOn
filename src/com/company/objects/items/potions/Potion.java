package com.company.objects.items.potions;

import com.company.objects.items.Item;

public abstract class Potion extends Item {
    final int power;
    public Potion(String name, int price, int power) {
        super(name, price);
        this.power = power;
    }

    public abstract void use();
}
