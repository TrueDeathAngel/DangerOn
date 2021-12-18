package com.company;

import com.company.map.CellTypes;
import com.googlecode.lanterna.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import static com.company.gameplay.GameLogic.*;

public class Main
{
    // static String[] heroNames = {"Олежа", "Васян", "Женёк", "Жорик"};

    public static DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
    public static Screen screen = null;
    public static TerminalSize terminalSize;
    public static boolean gameOver;

    public static CellTypes[][] map;

    public static void main(String[] args) {
        startGameLoop();

        /*Random = new Random();

        ArrayList<Mob> enemies = new ArrayList();
        for(int i = 0; i < 3; i++)
        {
            enemies.add(new Mob(
                    enemyNames[random.nextInt(enemyNames.length)],
                    random.nextInt(16) + 5,
                    random.nextInt(4) + 1,
                    random.nextInt(11) + 5,
                    new Weapon(weaponNames[random.nextInt(weaponNames.length)], random.nextInt(4) + 1, 1, 1)));
        }

        Hero hero = new Hero(
                heroNames[random.nextInt(heroNames.length)],
                30,
                2,
                14,
                0,
                2
        );

        Weapon weapon = new Weapon("Cool Sword", 9, 10, 1);
        hero.setWeapon(weapon);
        hero.showInfo();

        for (Mob mob : enemies)
        {
            mob.showInfo();
        }

        while(true)
        {
            random = new Random();

            ArrayList<Mob> mobRemoveList = new ArrayList();

            GameLogic.attack(hero, enemies.get(random.nextInt(enemies.size())));

            for (Mob mob : enemies)
            {
                if(!mob.isAlive())
                {
                    System.out.println("\n" + mob.getName() + " is dead");
                    mobRemoveList.add(mob);
                }
                else if(hero.isAlive()) GameLogic.attack(mob, hero);
            }

            if(!hero.isAlive())
            {
                System.out.println("\n" + hero.getName() + " is dead. Enemies won!");
                break;
            }

            for (Mob mob : mobRemoveList)
            {
                enemies.remove(mob);
            }
            mobRemoveList.clear();

            if(enemies.isEmpty())
            {
                System.out.println("\n" + hero.getName() + " won!");
                break;
            }
        }

        hero.addSpell(new Spell(new Ether()));
        hero.addSpell(new Spell(new Wind()));
        hero.addSpell(new Spell(new Fire()));

        hero.showSpells();*/
    }
}
