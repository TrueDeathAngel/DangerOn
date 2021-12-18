package com.company.objects.items.potions;

import com.company.gameplay.Controller;
import static com.company.gameplay.GameLogic.gamePlayControllers;

public abstract class LongTermPotion extends Potion {
    final int duration; // in seconds
    private int currentSecond = 0;

    public LongTermPotion(String name, int price, int power, int duration) {
        super(name, price, power);
        this.duration = duration;
    }

    @Override
    public void use() {
        Controller effectController = new Controller(1000) {
            @Override
            public void step() {
                if (currentSecond++ > duration) cancel();
                else doEffect();

                System.out.println(duration + " " + power);
            }
        };

        gamePlayControllers.add(effectController);
        effectController.pause();
        effectController.start();
    }

    public abstract void doEffect();
}
