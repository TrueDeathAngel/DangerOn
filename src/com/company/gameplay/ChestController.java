package com.company.gameplay;

import com.company.objects.items.Chest;

import static com.company.Main.map;
import static com.company.gameplay.GameLogic.floorEntities;
import static com.company.gameplay.GameLogic.hero;

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
    }
}
