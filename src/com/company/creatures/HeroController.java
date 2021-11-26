package com.company.creatures;

import com.company.GameLogic;
import com.company.Controller;
import com.googlecode.lanterna.input.KeyType;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static com.company.GameLogic.addToLog;
import static com.company.GameLogic.pressedKey;
import static com.company.Main.*;

public class HeroController extends Controller
{
    public Hero hero;
    public Creature target;
    ArrayList<Creature> targets;
    private int actionCounter = 0;

    public HeroController(Hero hero) { this.hero = hero; }

    @Override
    public void step() {
        if(gameOver) cancel();

        if(hero.getExperiencePoints() >= hero.getExperiencePointsForNextLevel()) {
            hero.newLevel();
            addToLog(hero.getName() + " reached new level: " + hero.getCurrentLevel() + '!');
        }

        if(GameLogic.autoMode) {
            if(actionCounter++ >= hero.getSlowness() * 10) {
                actionCounter = 0;
                if (target != null && target.isAlive()) {
                    if (target.scanArea(1)) hero.setStatus(Status.FIGHT);
                    else if (target.scanArea(hero.getScanRadius())) hero.setStatus(Status.CHASE);
                    else hero.setStatus(Status.IDLE);
                } else hero.setStatus(Status.IDLE);

                switch (hero.getStatus()) {
                    case IDLE -> {
                        target = null;
                        hero.move();
                        targets = hero.scanAreaForTargets();
                        if (targets.size() != 0) {
                            hero.setStatus(Status.CHASE);
                            target = targets.get(ThreadLocalRandom.current().nextInt(targets.size()));
                        }
                    }
                    case CHASE -> hero.moveToTarget(target);
                    case FIGHT -> target.receiveDamage(hero.getDamage());
                }
            }
        }
        else {
            try { TimeUnit.MILLISECONDS.sleep(1); }
            catch (InterruptedException ignored) {}
            if(pressedKey != null)
            {
                if(pressedKey.getKeyType() == KeyType.Escape || pressedKey.getKeyType() == KeyType.EOF)
                    cancel();
                else
                    switch (pressedKey.getKeyType())
                    {
                        case ArrowUp -> hero.move(-1, 0);
                        case ArrowRight -> hero.move(0, 1);
                        case ArrowDown -> hero.move(1, 0);
                        case ArrowLeft -> hero.move(0, -1);
                        case Character -> {
                            if(pressedKey.getCharacter() == 'd' || pressedKey.getCharacter() == 'в') {
                                targets = hero.scanAreaForTargets();
                                if(targets.size() > 0) {
                                    targets.stream()
                                            .filter(creature -> creature.scanArea(1))
                                            .findAny()
                                            .ifPresent(creature -> creature.receiveDamage(hero.getDamage()));
                                }
                            }
                        }
                    }
                pressedKey = null;
            }
        }
    }
}
