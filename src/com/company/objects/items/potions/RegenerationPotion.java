package com.company.objects.items.potions;

import static com.company.gameplay.GameLogic.hero;

public class RegenerationPotion extends LongTermPotion {
    public RegenerationPotion(String name, int price, int power, int duration) {
        super(name, price, power, duration);
    }

    @Override
    public void doEffect() {
        hero.heal(power);
    }
}
