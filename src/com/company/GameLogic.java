package com.company;

import com.company.creature.*;
import com.company.map.CellTypes;
import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static com.company.Main.*;

public class GameLogic
{
    public static boolean autoMode = false;

    public static ArrayList<Thread> controllers = new ArrayList<>();
    public static KeyStroke pressedKey = null;
    public static ArrayList<Creature> creatures = new ArrayList<>();

    public static Hero hero = new Hero("Oleg", 250, 5, 10);

    /*public static void attack(Mob attacker, Mob target)
    {
        System.out.println("\n" + attacker.getName() + " is trying to attack " + target.getName());
        if(ThreadLocalRandom.current().nextInt(20) + 1 > target.getDefencePoints())
        {
            float damage = DamagePerSecond(attacker);
            target.receiveDamage(damage);
            System.out.println(attacker.getName() + " deals damage equal to " + damage + " to " + target.getName());
        }
        else System.out.println(target.getName() + " dodges!");
    }*/

    public static void start()
    {
        for(int i = 0; i < 15; i++)
            creatures.add(GameObjectFactory.spawnCreature());
        for(int i = 0; i < 5; i++)
            creatures.add(GameObjectFactory.spawnMob());

        controllers.add(new Thread(new HeroController(hero)));

        controllers.add(new Thread(new KeyController()));

        creatures.forEach((creature) -> controllers.add(new Thread(new CreatureController(creature))));

        // Thread drawer = new Thread(() -> { while (true)
        // {
        //     GameLogic.drawField();
        //     try
        //     {
        //         Thread.sleep(1000);
        //     }
        //     catch (InterruptedException e) {}
        // }});

        // drawer.start();

        controllers.forEach(Thread::start);
    }

    public static void drawMap()
    {
        for(int i = 0; i < map.length; i++)
            for (int j = 0; j < map[i].length; j++)
                switch (map[i][j])
                {
                    case EMPTY, SAFE_AREA -> screen.setCharacter(j + 1, i + 1, new TextCharacter(' '));
                    case LAVA -> screen.setCharacter(j + 1, i + 1, new TextCharacter(
                            '▉',
                            new TextColor.RGB(ThreadLocalRandom.current().nextInt(15) + 96, 8, 16),
                            TextColor.ANSI.DEFAULT));
                    case VERTICAL_WALL -> screen.setCharacter(j + 1, i + 1, new TextCharacter(Symbols.BOLD_SINGLE_LINE_VERTICAL));
                    case HORIZONTAL_WALL -> screen.setCharacter(j + 1, i + 1, new TextCharacter(Symbols.BOLD_SINGLE_LINE_HORIZONTAL));
                    case TOP_LEFT_CORNER_WALL -> screen.setCharacter(j + 1, i + 1, new TextCharacter('┏'));
                    case TOP_RIGHT_CORNER_WALL -> screen.setCharacter(j + 1, i + 1, new TextCharacter('┓'));
                    case BOTTOM_RIGHT_CORNER_WALL -> screen.setCharacter(j + 1, i + 1, new TextCharacter('┛'));
                    case BOTTOM_LEFT_CORNER_WALL -> screen.setCharacter(j + 1, i + 1, new TextCharacter('┗'));
                    case DOOR -> screen.setCharacter(j + 1, i + 1, new TextCharacter('&'));
                }

        drawRectangle(new Point(0, 0), new Point(map[0].length + 1, map.length + 1));

        creatures.forEach((creature) ->
                screen.setCharacter(
                        creature.getPosition().y + 1,
                        creature.getPosition().x + 1,
                        new TextCharacter(
                                creature.getModel(),
                                new TextColor.RGB(200, 0, 10),
                                TextColor.ANSI.DEFAULT)
                )
        );

        screen.setCharacter(hero.getPosition().y + 1, hero.getPosition().x + 1, new TextCharacter(
                '@',
                new TextColor.RGB(0, 200, 100),
                TextColor.ANSI.DEFAULT));
    }

    private static void drawRectangle(Point topLeft, Point bottomRight)
    {
        TextGraphics textGraphics = screen.newTextGraphics();

        textGraphics.drawLine(
                topLeft.x + 1,
                topLeft.y,
                bottomRight.x - 1,
                topLeft.y,
                Symbols.DOUBLE_LINE_HORIZONTAL);

        textGraphics.drawLine(
                topLeft.x + 1,
                bottomRight.y,
                bottomRight.x - 1,
                bottomRight.y,
                Symbols.DOUBLE_LINE_HORIZONTAL);

        textGraphics.drawLine(
                topLeft.x,
                topLeft.y + 1,
                topLeft.x,
                bottomRight.y - 1,
                Symbols.DOUBLE_LINE_VERTICAL);

        textGraphics.drawLine(
                bottomRight.x,
                topLeft.y + 1,
                bottomRight.x,
                bottomRight.y - 1,
                Symbols.DOUBLE_LINE_VERTICAL);

        textGraphics.setCharacter(topLeft.x, topLeft.y, Symbols.DOUBLE_LINE_TOP_LEFT_CORNER);
        textGraphics.setCharacter(bottomRight.x, topLeft.y, Symbols.DOUBLE_LINE_TOP_RIGHT_CORNER);
        textGraphics.setCharacter(bottomRight.x, bottomRight.y, Symbols.DOUBLE_LINE_BOTTOM_RIGHT_CORNER);
        textGraphics.setCharacter(topLeft.x, bottomRight.y, Symbols.DOUBLE_LINE_BOTTOM_LEFT_CORNER);
    }

    public static void drawInfo()
    {
        String sizeLabel = "Player's HP: " + GameLogic.hero.getHitPoints();
        if(terminalSize.getColumns() - map[0].length - sizeLabel.length() - 10 < 0) return;

        TerminalPosition labelBoxTopLeft =
                new TerminalPosition(map[0].length + 8, 0);
        TerminalSize labelBoxSize = new TerminalSize(sizeLabel.length() + 2, 3);
        TextGraphics textGraphics = screen.newTextGraphics();

        textGraphics.fillRectangle(labelBoxTopLeft, labelBoxSize, ' ');

        drawRectangle(new Point(map[0].length + 8, 0), new Point(map[0].length + sizeLabel.length() + 9, 2));

        textGraphics.putString(labelBoxTopLeft.withRelative(1, 1), sizeLabel);
    }

    public static Point getFreePoint()
    {
        Random random = new Random();
        int x, y;
        while (true)
        {
            x = random.nextInt(map.length);
            y = random.nextInt(map[x].length);
            if(map[x][y] == CellTypes.EMPTY) return new Point(x ,y);
        }
    }
}
