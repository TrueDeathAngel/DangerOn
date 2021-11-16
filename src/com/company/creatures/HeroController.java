package com.company.creatures;

import com.company.GameLogic;
import com.googlecode.lanterna.input.KeyType;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static com.company.GameLogic.pressedKey;
import static com.company.Main.*;

public class HeroController implements Runnable
{
    public Hero hero;
    public Creature target;
    ArrayList<Creature> targets;

    public HeroController(Hero hero) { this.hero = hero; }

    @Override
    public void run()
    {
        while (!gameOver)
        {
            if(!hero.isAlive())
            {
                map[hero.position.x][hero.position.y] = hero.underCell;
                System.out.println("\u001b[38;5;9m" + hero.getName() + "\u001b[0m was slain");
                break;
            }

            if(!GameLogic.autoMode)
            {
                try { TimeUnit.MILLISECONDS.sleep(1); }
                catch (InterruptedException ignored) {}
                if(pressedKey != null)
                {
                    if(pressedKey.getKeyType() == KeyType.Escape || pressedKey.getKeyType() == KeyType.EOF)
                        break;
                    else
                        switch (pressedKey.getKeyType())
                        {
                            case ArrowUp -> hero.move(-1, 0);
                            case ArrowRight -> hero.move(0, 1);
                            case ArrowDown -> hero.move(1, 0);
                            case ArrowLeft -> hero.move(0, -1);
                            case Character -> {
                                if(pressedKey.getCharacter() == 'd')
                                {
                                    targets = hero.scanAreaForTargets();
                                    if(targets.size() > 0)
                                    {
                                        //targets.get(ThreadLocalRandom.current().nextInt(targets.size())).receiveDamage(hero.getDamage());
                                        targets.stream()
                                                .filter(creature -> creature.scanArea(1))
                                                .findAny()
                                                .ifPresent(creature -> creature.receiveDamage(hero.getDamage()));
                                    }
                                }
                                //else if(pressedKey.getCharacter() == 'a') GameLogic.autoMode = true;
                            }
                        }
                    pressedKey = null;
                }
            }
            else
            {
                if(target != null && target.isAlive())
                {
                    if(target.scanArea(1)) hero.setStatus(Status.FIGHT);
                    else if(target.scanArea(hero.getScanRadius())) hero.setStatus(Status.CHASE);
                    else hero.setStatus(Status.IDLE);
                }
                else hero.setStatus(Status.IDLE);

                switch (hero.getStatus())
                {
                    case IDLE -> {
                        target = null;
                        hero.move();
                        targets = hero.scanAreaForTargets();
                        if(targets.size() != 0)
                        {
                            hero.setStatus(Status.CHASE);
                            target = targets.get(ThreadLocalRandom.current().nextInt(targets.size()));
                        }
                    }
                    case CHASE -> hero.moveToTarget(target);
                    case FIGHT -> target.receiveDamage(hero.getDamage());
                }
                try
                {
                    TimeUnit.MILLISECONDS.sleep(hero.getSlowness() * 100L);
                }
                catch (InterruptedException ignored) {}
            }
        }
    }
}
