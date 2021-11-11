package com.company;

import com.company.map.CellTypes;
import com.company.map.MapFactory;
import com.googlecode.lanterna.*;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static com.company.GameLogic.*;

public class Main
{
    static String[] heroNames = {"Олежа", "Васян", "Женёк", "Жорик"};
    static String[] weaponNames = {"Sword", "Axe", "Pickaxe", "Bow", "Crossbow", "Spear", "Dagger", "Mace", "Hammer", "Sickle"};

    public static DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
    public static Screen screen = null;
    public static TerminalSize terminalSize;
    public static boolean gameOver;

    public static CellTypes[][] map;

    public static void main(String[] args)
    {
        /*int i = 1024 * 10;
        int j = i + 1024;
        while(i < j)
        {
            try
            {
                System.out.println(i + " " + (char) i);
                i++;
            }
            catch(Exception e)
            {
                System.out.println(e);
            }
        }*/

        /*CellTypes[][] map = MapFactory.createMap();

        for (CellTypes[] row : map)
        {
            for (CellTypes cell : row)
                switch (cell) {
                    case EMPTY -> System.out.print(' ');
                    case LAVA -> System.out.print(0);
                    case VERTICAL_WALL -> System.out.print('║');
                    case HORIZONTAL_WALL -> System.out.print('═');
                    case TOP_LEFT_CORNER_WALL -> System.out.print('╔');
                    case TOP_RIGHT_CORNER_WALL -> System.out.print('╗');
                    case BOTTOM_RIGHT_CORNER_WALL -> System.out.print('╝');
                    case BOTTOM_LEFT_CORNER_WALL -> System.out.print('╚');
                }
            System.out.println();
        }*/

        map = MapFactory.createMap();

        try
        {
            defaultTerminalFactory.setTerminalEmulatorTitle("Game " + GameResources.version);
            try {
                // Load a font and set its size
                Font font = Font.createFont(Font.TRUETYPE_FONT, new File("Hack-Regular.ttf")).deriveFont(12f);

                // Set the font for the terminal factory
                defaultTerminalFactory.setTerminalEmulatorFontConfiguration(SwingTerminalFontConfiguration.newInstance(font));
            }
            // Handle exceptions :)
            catch (FontFormatException e)
            {
                System.out.println("Unable to set the font: Wrong font format");
            }

            screen = defaultTerminalFactory.createScreen();

            screen.startScreen();
            screen.setCursorPosition(null);

            terminalSize = screen.getTerminalSize();

            GameLogic.start();

            while (true)
            {
                TerminalSize newSize = screen.doResizeIfNecessary();
                if(newSize != null)
                {
                    terminalSize = newSize;
                    screen.clear();
                }

                if(gameOver) break;

                drawMap();

                drawInfo();

                screen.refresh();
                //Thread.yield();
            }
        }
        catch (IOException ignored) {}
        finally {
            if (screen != null) {
                try {
                    screen.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            GameLogic.controllers.forEach(Thread::stop);
        }

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
