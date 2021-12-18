package com.company.objects.items.potions;

import static com.company.gameplay.GameLogic.hero;

public class HealingPotion extends Potion {
    public HealingPotion(String name, int price, int power) {
        super(name, price, power);
    }

    @Override
    public void use() {
        hero.heal(power);
    }
}
