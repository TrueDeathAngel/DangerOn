package com.company.gameplay;

import com.company.map.CellTypes;
import com.company.objects.creatures.Creature;
import com.company.objects.creatures.Status;
import com.company.objects.items.Chest;
import com.company.recources.Colors;

import java.util.concurrent.ThreadLocalRandom;

import static com.company.Main.*;
import static com.company.gameplay.GameLogic.*;

public class CreatureController extends Controller
{
    public Creature creature;
    private int actionCounter;

    public CreatureController(Creature creature) { this.creature = creature; }

    @Override
    public void step() {
        if(!creature.isAlive()) {
            creature.die();
            cancel();
        }
        if(actionCounter++ >= creature.getSlowness() * 10) {
            actionCounter = 0;

            if(creature.scanArea()) creature.setStatus(Status.CHASE);
            else creature.setStatus(Status.IDLE);
            if(creature.isCloseToHero()) creature.setStatus(Status.FIGHT);
            switch (creature.getStatus()) {
                case IDLE -> { if(ThreadLocalRandom.current().nextInt(4) == 3) creature.move(); }
                case CHASE -> creature.moveToTarget(hero);
                case FIGHT -> hero.receiveDamage(creature.getAttackPower());
            }
        }
    }
}
