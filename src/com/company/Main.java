package com.company;

import com.company.items.Weapon;
import com.company.map.CellTypes;
import com.company.recources.Colors;
import com.company.recources.GameResources;
import com.googlecode.lanterna.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static com.company.GameLogic.*;

public class Main
{
    // static String[] heroNames = {"Олежа", "Васян", "Женёк", "Жорик"};

    public static DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
    public static Screen screen = null;
    public static TerminalSize terminalSize;
    public static boolean gameOver;

    public static CellTypes[][] map;

    public static void main(String[] args) {
        try {
            defaultTerminalFactory.setTerminalEmulatorTitle("DungerOn " + GameResources.version);
            try {
                // Load a font and set its size
                Font font = Font.createFont(Font.TRUETYPE_FONT, new File("SourceCodePro-Medium.ttf")).deriveFont(20f);

                // Set the font for the terminal factory
                defaultTerminalFactory.setTerminalEmulatorFontConfiguration(SwingTerminalFontConfiguration.newInstance(font));
            }
            // Handle exception :)
            catch (FontFormatException e) { System.out.println("Unable to set the font: Wrong font format"); }

            screen = defaultTerminalFactory.createScreen();

            screen.startScreen();
            screen.setCursorPosition(null);

            terminalSize = screen.getTerminalSize();

            hero.setWeapon(new Weapon("Cool Stick", 4, 1, 1));

            GameLogic.startGame();

            while (true) {
                TerminalSize newSize = screen.doResizeIfNecessary();
                if(newSize != null) {
                    terminalSize = newSize;
                    screen.clear();
                }

                if(gameOver) break;

                if(!hero.isAlive()) {
                    addToLog(Colors.GREEN + hero.getName() + Colors.RESET + " was slain");
                    floorNumber = 0;
                    refreshMap();
                    hero.instantRecovery();
                }

                if(lastKeys.contains("gimme")) {
                    isAdmin = !isAdmin;
                    if(isAdmin)
                        addToLog("Administrator rights have been issued to " + hero.getName());
                    else {
                        addToLog("Administrator rights were taken away from " + hero.getName());
                        noWarFog = false;
                    }
                    lastKeys = "";
                }

                drawMap();

                drawAllData();

                screen.refresh();
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

            enemiesControllers.forEach(Controller::cancel);
            gamePlayControllers.forEach(Controller::cancel);
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
