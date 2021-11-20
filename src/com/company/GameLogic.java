package com.company;

import com.company.creatures.*;
import com.company.gameplay.EffectsController;
import com.company.gameplay.KeyController;
import com.company.map.MapFactory;
import com.company.recources.Colors;
import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import static com.company.Main.*;
import static com.company.gameplay.EffectsController.getLavaColor;

public class GameLogic
{
    public static boolean autoMode = false;
    public static int floorNumber;

    public static ArrayList<Thread> enemiesControllers = new ArrayList<>();
    public static ArrayList<Thread> gamePlayControllers = new ArrayList<>();
    public static KeyStroke pressedKey = null;
    public static ArrayList<Creature> creatures = new ArrayList<>();

    public static int counter;

    public static Hero hero = new Hero("Player", 100, 5, 10);

    private static final TerminalSize heroStatsPanelSize = new TerminalSize(21, 8);
    private static final int mapToMenuDistanceHorizontal = 8;
    private static ArrayList<String> stringsToDraw = new ArrayList<>(); // The first element is a header
    private static final ArrayList<String> log = new ArrayList<>();

    public static String lastKeys = "";
    public static boolean isAdmin = false;

    private static boolean[][] visibleCells;
    public static boolean noWarFog = false;

    private static int logWidth = 30;

    public static void addToLog(String text) {
        if(log.size() >= 5) log.remove(0);
        log.add(text);
    }

    public static void addPressedKey(char key) {
        if(lastKeys.length() >= 10) lastKeys = lastKeys.substring(1);
        lastKeys += key;
    }

    public static void startGame() {
        refreshMap();

        gamePlayControllers.add(new Thread(new EffectsController()));

        gamePlayControllers.add(new Thread(new HeroController(hero)));

        gamePlayControllers.add(new Thread(new KeyController()));

        gamePlayControllers.forEach(Thread::start);
    }

    public static void refreshMap() {
        GameLogic.addToLog(hero.getName() + " entered the floor #" + ++floorNumber);

        map = MapFactory.createMap();
        visibleCells = new boolean[MapFactory.getMapHeightInCells()][MapFactory.getMapWidthInCells()];
        hero.setStartPosition();

        addEnemies();

        screen.clear();
    }

    public static void addEnemies() {
        enemiesControllers.forEach(Thread::stop);
        enemiesControllers.clear();
        creatures.clear();

        int numberOfEnemies = ThreadLocalRandom.current().nextInt(MapFactory.getNumberOfRooms()) + 3;

        for(int i = 0; i < 3 * numberOfEnemies / 4; i++)
            creatures.add(GameObjectFactory.spawnCreature());
        for(int i = 0; i < numberOfEnemies - 3 * numberOfEnemies / 4; i++)
            creatures.add(GameObjectFactory.spawnMob());

        creatures.forEach((Creature::setStartPosition));

        creatures.forEach((creature) -> enemiesControllers.add(new Thread(new CreatureController(creature))));

        enemiesControllers.forEach(Thread::start);
    }

    public static void drawMap() {
        for(int i = Math.max(hero.getPosition().x - hero.getScanRadius(), 0); i < Math.min(hero.getPosition().x + hero.getScanRadius(), map.length); i++)
            for(int j = Math.max(hero.getPosition().y - hero.getScanRadius() * 2, 0); j < Math.min(hero.getPosition().y + hero.getScanRadius() * 2, map[0].length); j++)
                visibleCells[i][j] = true;

        try {
            for (int i = 0; i < map.length; i++)
                for (int j = 0; j < map[i].length; j++)
                    if(!(noWarFog || visibleCells[i][j]))
                        screen.setCharacter(j + 1, i + 2, new TextCharacter('#'));
                    else
                        switch (map[i][j]) {
                            case EMPTY, SAFE_AREA -> screen.setCharacter(j + 1, i + 2, new TextCharacter(' '));
                            case LAVA -> screen.setCharacter(j + 1, i + 2, new TextCharacter(
                                    '▉',
                                    getLavaColor(),
                                    TextColor.ANSI.DEFAULT));
                            case VERTICAL_WALL -> screen.setCharacter(j + 1, i + 2, new TextCharacter(Symbols.BOLD_SINGLE_LINE_VERTICAL));
                            case HORIZONTAL_WALL -> screen.setCharacter(j + 1, i + 2, new TextCharacter(Symbols.BOLD_SINGLE_LINE_HORIZONTAL));
                            case TOP_LEFT_CORNER_WALL -> screen.setCharacter(j + 1, i + 2, new TextCharacter('┏'));
                            case TOP_RIGHT_CORNER_WALL -> screen.setCharacter(j + 1, i + 2, new TextCharacter('┓'));
                            case BOTTOM_RIGHT_CORNER_WALL -> screen.setCharacter(j + 1, i + 2, new TextCharacter('┛'));
                            case BOTTOM_LEFT_CORNER_WALL -> screen.setCharacter(j + 1, i + 2, new TextCharacter('┗'));
                            case DOOR -> screen.setCharacter(j + 1, i + 2, new TextCharacter('&'));
                        }
        } catch (ArrayIndexOutOfBoundsException ignored) {}

        drawRectangle(new TerminalPosition(0, 0), new TerminalSize(map[0].length + 1, map.length + 1), "Floor #" + floorNumber);

        try {
            creatures.forEach((creature) -> {
                if(creature.getPosition() != null && (noWarFog || visibleCells[creature.getPosition().x][creature.getPosition().y]))
                    screen.setCharacter(
                            creature.getPosition().y + 1,
                            creature.getPosition().x + 2,
                            new TextCharacter(
                                    creature.getModel(),
                                    new TextColor.RGB(200, 0, 10),
                                    TextColor.ANSI.DEFAULT)
                    );
            }
            );
        }
        catch (ConcurrentModificationException ignored) {}

        screen.setCharacter(hero.getPosition().y + 1, hero.getPosition().x + 2, new TextCharacter(
                '@',
                new TextColor.RGB(0, 200, 100),
                TextColor.ANSI.DEFAULT));
    }

    private static void drawRectangle(TerminalPosition topLeft, TerminalSize size) {
        TextGraphics textGraphics = screen.newTextGraphics();

        textGraphics.drawLine(
                topLeft.getColumn() + 1,
                topLeft.getRow(),
                topLeft.getColumn() + size.getColumns() - 2,
                topLeft.getRow(),
                Symbols.DOUBLE_LINE_HORIZONTAL);

        textGraphics.drawLine(
                topLeft.getColumn() + 1,
                topLeft.getRow() + size.getRows() - 1,
                topLeft.getColumn() + size.getColumns() - 1,
                topLeft.getRow() + size.getRows() - 1,
                Symbols.DOUBLE_LINE_HORIZONTAL);

        textGraphics.drawLine(
                topLeft.getColumn(),
                topLeft.getRow() + 1,
                topLeft.getColumn(),
                topLeft.getRow() + size.getRows() - 2,
                Symbols.DOUBLE_LINE_VERTICAL);

        textGraphics.drawLine(
                topLeft.getColumn() + size.getColumns() - 1,
                topLeft.getRow() + 1,
                topLeft.getColumn() + size.getColumns() - 1,
                topLeft.getRow() + size.getRows() - 2,
                Symbols.DOUBLE_LINE_VERTICAL);

        textGraphics.setCharacter(topLeft, Symbols.DOUBLE_LINE_TOP_LEFT_CORNER);
        textGraphics.setCharacter(topLeft.withRelativeColumn(size.getColumns() - 1), Symbols.DOUBLE_LINE_TOP_RIGHT_CORNER);
        textGraphics.setCharacter(topLeft.withRelative(size.getColumns() - 1, size.getRows() - 1), Symbols.DOUBLE_LINE_BOTTOM_RIGHT_CORNER);
        textGraphics.setCharacter(topLeft.withRelativeRow(size.getRows() - 1), Symbols.DOUBLE_LINE_BOTTOM_LEFT_CORNER);
    }

    private static void drawRectangle(TerminalPosition topLeft, TerminalSize size, String header) {
        TextGraphics textGraphics = screen.newTextGraphics();

        textGraphics.putString(topLeft, "-".repeat(size.getColumns()));
        textGraphics.putString(topLeft.withRelativeColumn((size.getColumns() - header.length()) / 2), header);

        drawRectangle(topLeft.withRelativeRow(1), size);
    }

    public static void drawData(TerminalPosition topLeft, TerminalSize size) {
        TextGraphics textGraphics = screen.newTextGraphics();
        textGraphics.fillRectangle(topLeft, size.withColumns(terminalSize.getColumns()).withRelativeRows(1), ' ');

        drawRectangle(
                topLeft,
                size.withColumns(stringsToDraw.stream().mapToInt((s) -> s.replaceAll("\u001b\\[[0-9;]*m", "").length()).max().orElse(0)).withRelativeColumns(2),
                stringsToDraw.get(0)
        );

        for(int i = 1; i < stringsToDraw.size(); i++) {
            textGraphics.putCSIStyledString(topLeft.withRelative(1, i + 1), stringsToDraw.get(i));
        }

        stringsToDraw.clear();
    }

    public static void drawAllData() {
        // Draw hero's stats
        stringsToDraw = new ArrayList<>(List.of(
                hero.getName(),
                "HP:" + Colors.RED
                        + " ❤".repeat((int)(10 * (float)hero.getHitPoints() / (float)hero.getMaxHitPoints()))
                        + ((10 * hero.getHitPoints()) % hero.getMaxHitPoints() != 0 ? Colors.RED_DANCING + " ❤" : "")
                        + Colors.RESET
                        + " ❤".repeat((int)(10 - 10 * (float)hero.getHitPoints() / (float)hero.getMaxHitPoints()))
                        + " ("
                        + hero.getHitPoints()
                        + '/'
                        + hero.getMaxHitPoints()
                        + ')',
                "Defence Points: " + hero.getDefencePoints(),
                "Attack Power: " + (hero.getAttackPower() + hero.getWeaponAttackPower()),
                "Current Level: " + hero.getCurrentLevel(),
                "Experience Points: " + hero.getExperiencePoints() + '/' + hero.getExperiencePointsForNextLevel(),
                "Number of enemies: " + creatures.size()
        ));

        drawData(new TerminalPosition(map[0].length + mapToMenuDistanceHorizontal + 1, 0), new TerminalSize(24, 8));

        // Draw controls
        stringsToDraw = new ArrayList<>(List.of(
                "Controls",
                "D - attack",
                "A - auto mode",
                "R - regenerate map",
                "Esc - exit"
        ));

        drawData(new TerminalPosition(map[0].length + mapToMenuDistanceHorizontal + 1, 10), new TerminalSize(20, 6));

        // Draw log
        stringsToDraw = new ArrayList<>(List.of("Log"));
        stringsToDraw.addAll(log);

        drawData(new TerminalPosition(0, map.length + 3), new TerminalSize(20, log.size() + 2));
    }
}
