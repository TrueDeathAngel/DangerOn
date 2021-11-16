package com.company;

import com.company.creatures.*;
import com.company.gameplay.EffectsController;
import com.company.gameplay.KeyController;
import com.company.map.MapFactory;
import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;

import java.awt.*;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.concurrent.ThreadLocalRandom;

import static com.company.Main.*;
import static com.company.gameplay.EffectsController.getLavaColor;

public class GameLogic
{
    public static boolean autoMode = false;

    public static ArrayList<Thread> enemiesControllers = new ArrayList<>();
    public static ArrayList<Thread> gamePlayControllers = new ArrayList<>();
    public static KeyStroke pressedKey = null;
    public static ArrayList<Creature> creatures = new ArrayList<>();

    public static Hero hero = new Hero("Oleg", 250, 5, 10);

    public static void startGame()
    {
        refreshMap();

        gamePlayControllers.add(new Thread(new EffectsController()));

        gamePlayControllers.add(new Thread(new HeroController(hero)));

        gamePlayControllers.add(new Thread(new KeyController()));

        gamePlayControllers.forEach(Thread::start);
    }

    public static void refreshMap()
    {
        map = MapFactory.createMap();
        hero.setStartPosition();

        addEnemies();

        screen.clear();
    }

    public static void addEnemies()
    {
        enemiesControllers.forEach(Thread::stop);
        enemiesControllers.clear();
        creatures.clear();

        int numberOfEnemies = ThreadLocalRandom.current().nextInt(MapFactory.getNumberOfRooms()) + 3;

        for(int i = 0; i < 3 * numberOfEnemies / 4; i++)
            creatures.add(GameObjectFactory.spawnCreature());
        for(int i = 0; i < numberOfEnemies / 4; i++)
            creatures.add(GameObjectFactory.spawnMob());

        creatures.forEach((Creature::setStartPosition));

        creatures.forEach((creature) -> enemiesControllers.add(new Thread(new CreatureController(creature))));

        enemiesControllers.forEach(Thread::start);
    }

    public static void drawMap() throws ArrayIndexOutOfBoundsException
    {
        for(int i = 0; i < map.length; i++)
            for (int j = 0; j < map[i].length; j++)
                switch (map[i][j])
                {
                    case EMPTY, SAFE_AREA -> screen.setCharacter(j + 1, i + 1, new TextCharacter(' '));
                    case LAVA -> screen.setCharacter(j + 1, i + 1, new TextCharacter(
                            '▉',
                            getLavaColor(),
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

        try {
            creatures.forEach((creature) -> {
                if(creature.getPosition() != null)
                    screen.setCharacter(
                            creature.getPosition().y + 1,
                            creature.getPosition().x + 1,
                            new TextCharacter(
                                    creature.getModel(),
                                    new TextColor.RGB(200, 0, 10),
                                    TextColor.ANSI.DEFAULT)
                    );
            }
            );
        }
        catch (ConcurrentModificationException ignored) {}

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
}
