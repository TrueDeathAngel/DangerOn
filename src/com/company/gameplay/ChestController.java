package com.company.gameplay;

import com.company.objects.items.chests.Chest;

import java.util.Objects;

public class ChestController extends Controller {
    private final Chest chest;

    public ChestController(Chest chest) {
        this.chest = chest;
    }

    @Override
    public void step() {
        if(!chest.isAlive()) {
            chest.die();
            cancel();
        }
        if(chest.inventory.getItems().stream().anyMatch(Objects::isNull)) {System.out.println(chest.inventory.getName());
        System.out.println(" has null item!");}
    }
}
