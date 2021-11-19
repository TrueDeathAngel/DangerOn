package com.company.creatures;

import com.company.GameLogic;
import com.company.recources.Colors;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static com.company.Main.*;

public class CreatureController implements Runnable
{
    public Creature creature;

    public CreatureController(Creature creature) { this.creature = creature; }

    @Override
    public void run()
    {
        while (!gameOver)
        {
            if(!creature.isAlive())
            {
                map[creature.position.x][creature.position.y] = creature.underCell;
                GameLogic.creatures.remove(creature);
                GameLogic.addToLog(Colors.RED + creature.getName() + Colors.RESET + " was slain");
                GameLogic.hero.addExperiencePoints(creature.getCost());
                GameLogic.numberOfEnemies--;
                break;
            }

            if(creature.scanArea()) creature.setStatus(Status.CHASE);
            else creature.setStatus(Status.IDLE);
            if(creature.isCloseToHero()) creature.setStatus(Status.FIGHT);
                switch (creature.getStatus())
                {
                    case IDLE -> { if(ThreadLocalRandom.current().nextInt(4) == 3) creature.move(); }
                    case CHASE -> creature.moveToTarget(GameLogic.hero);
                    case FIGHT -> GameLogic.hero.receiveDamage(creature.getAttackPower());
                }
            try
            {
                TimeUnit.MILLISECONDS.sleep(creature.getSlowness() * 100L);
            }
            catch (InterruptedException ignored) {}
        }
    }
}
