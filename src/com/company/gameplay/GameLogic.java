package com.company.gameplay;

import com.company.objects.GameEntity;
import com.company.objects.GameObjectFactory;
import com.company.objects.creatures.*;
import com.company.objects.items.chests.Chest;
import com.company.objects.items.Equipment;
import com.company.objects.items.Item;
import com.company.objects.items.Weapon;
import com.company.map.MapFactory;
import com.company.objects.items.potions.Potion;
import com.company.recources.colors.StringColors;
import com.company.recources.GameResources;
import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.company.Main.*;
import static com.company.gameplay.ControllerFactory.getSuitableController;
import static com.company.gameplay.LavaEffectController.getLavaColor;

public class GameLogic
{
    public static boolean autoMode = false;
    public static int floorNumber;

    public static ArrayList<Controller> floorEntitiesControllers = new ArrayList<>();
    public static ArrayList<Controller> gamePlayControllers = new ArrayList<>();
    public static KeyController keyController = new KeyController();
    public static KeyStroke pressedKey = null;

    public static ArrayList<GameEntity> floorEntities = new ArrayList<>();

    public static Hero hero = new Hero("Player", 100, 5, 10);

    private static final Point heroViewZone = new Point(18, 64);
    private static final int mapToMenuDistanceHorizontal = 8;
    protected static ArrayList<String> stringsToDraw = new ArrayList<>(); // The first element is a header
    private static final ArrayList<String> log = new ArrayList<>();

    public static String lastKeys = "";
    public static boolean isAdmin = false;
    private static boolean[][] visibleCells;
    public static boolean noWarFog = false;

    public static boolean isOpenedInventory = false;
    public static Chest openedChest;
    public static int inventoryCursorPosition = 0;
    public static boolean inventoryWindowIsActive = true;

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

        gamePlayControllers.add(new LavaEffectController());

        gamePlayControllers.add(new HeroController(hero));

        gamePlayControllers.add(new Controller(1000) {
            @Override
            public void step() {
                hero.heal(hero.regenerationPower);
            }
        });

        keyController.start();

        gamePlayControllers.forEach(Controller::start);
    }

    public static void refreshMap() {
        GameLogic.addToLog(hero.getName() + " entered the floor #" + ++floorNumber);

        map = MapFactory.createMap();
        visibleCells = new boolean[MapFactory.getMapHeightInCells()][MapFactory.getMapWidthInCells()];
        hero.setPosition();

        addFloorEntities();

        screen.clear();
    }

    public static void addFloorEntities() {
        floorEntitiesControllers.forEach(Controller::cancel);
        floorEntitiesControllers.clear();
        floorEntities.clear();

        int numberOfEnemies = ThreadLocalRandom.current().nextInt(MapFactory.getNumberOfRooms() * 10) + 3;

        for(int i = 0; i < 3 * numberOfEnemies / 4; i++)
            floorEntities.add(GameObjectFactory.spawnCreature());
        for(int i = 0; i < numberOfEnemies - 3 * numberOfEnemies / 4; i++)
            floorEntities.add(GameObjectFactory.spawnMob());

        for (int i = 0; i < MapFactory.getNumberOfRooms() / 4; i++)
            floorEntities.add(GameObjectFactory.spawnChest());

        floorEntities.forEach((GameEntity::setPosition));

        floorEntities.forEach((entity) -> floorEntitiesControllers.add(getSuitableController(entity)));

        floorEntitiesControllers.forEach(Controller::start);
    }

    private static void drawFloorEntities() throws ConcurrentModificationException {
        floorEntities.forEach((creature) -> {
                    if(creature != null && creature.getPosition() != null
                            && creature.getPosition().y >= Math.min(hero.getPosition().y - heroViewZone.y / 2, map[0].length - heroViewZone.y)
                            && creature.getPosition().y < Math.max(hero.getPosition().y + heroViewZone.y / 2, heroViewZone.y)
                            && creature.getPosition().x >= Math.min(hero.getPosition().x - heroViewZone.x / 2, map.length - heroViewZone.x)
                            && creature.getPosition().x < Math.max(hero.getPosition().x + heroViewZone.x / 2, heroViewZone.x)
                            && (noWarFog || visibleCells[creature.getPosition().x][creature.getPosition().y]))
                        screen.setCharacter(
                                Math.max(0, hero.getPosition().y - map[0].length + heroViewZone.y / 2)
                                        + Math.min(heroViewZone.y / 2, hero.getPosition().y)
                                        + (creature.getPosition().y - hero.getPosition().y) + 1,
                                Math.max(0, hero.getPosition().x - map.length + heroViewZone.x / 2)
                                        + Math.min(heroViewZone.x / 2, hero.getPosition().x)
                                        + (creature.getPosition().x - hero.getPosition().x) + 2,
                                creature.model
                        );
                });
    }

    public static void drawMap() {
        screen.newTextGraphics().fillRectangle(new TerminalPosition(0, 0), terminalSize, ' ');

        for(int i = Math.max(hero.getPosition().x - hero.getScanRadius() + 1, 0); i < Math.min(hero.getPosition().x + hero.getScanRadius(), map.length); i++)
            for(int j = Math.max(Math.abs(i - hero.getPosition().x) + hero.getPosition().y - hero.getScanRadius() * 2 + 1, 0); j < Math.min(hero.getPosition().y + hero.getScanRadius() * 2 - Math.abs(i - hero.getPosition().x), map[0].length); j++)
                visibleCells[i][j] = true;

        try {
            for (int i = 0; i < heroViewZone.x; i++)
                for (int j = 0; j < heroViewZone.y; j++)
                    if(!(noWarFog || visibleCells[i + Math.max(0, Math.min(hero.getPosition().x - heroViewZone.x / 2, map.length - heroViewZone.x))]
                            [j + Math.max(0, Math.min(hero.getPosition().y - heroViewZone.y / 2, map[0].length - heroViewZone.y))]))
                    screen.setCharacter(j + 1, i + 2, new TextCharacter('#'));
                    else
                        screen.setCharacter(j + 1, i + 2,
                                switch (map[i + Math.max(0, Math.min(hero.getPosition().x - heroViewZone.x / 2, map.length - heroViewZone.x))]
                                [j + Math.max(0, Math.min(hero.getPosition().y - heroViewZone.y / 2, map[0].length - heroViewZone.y))]) {
                                    case EMPTY, SAFE_AREA, CREATURE, HERO, CHEST -> new TextCharacter(' ');
                            case LAVA -> new TextCharacter(
                                    '▉',
                                    getLavaColor(),
                                    TextColor.ANSI.DEFAULT);
                            case VERTICAL_WALL -> new TextCharacter(Symbols.BOLD_SINGLE_LINE_VERTICAL);
                            case HORIZONTAL_WALL -> new TextCharacter(Symbols.BOLD_SINGLE_LINE_HORIZONTAL);
                            case TOP_LEFT_CORNER_WALL -> new TextCharacter('┏');
                            case TOP_RIGHT_CORNER_WALL -> new TextCharacter('┓');
                            case BOTTOM_RIGHT_CORNER_WALL -> new TextCharacter('┛');
                            case BOTTOM_LEFT_CORNER_WALL -> new TextCharacter('┗');
                            case DOOR -> new TextCharacter('&');
                            case EXIT -> new TextCharacter('E', TextColor.ANSI.GREEN, TextColor.ANSI.DEFAULT);
                        });
        } catch (ArrayIndexOutOfBoundsException ignored) {}

        drawRectangle(new TerminalPosition(0, 0), new TerminalSize(heroViewZone.y + 2, heroViewZone.x + 2), "Floor #" + floorNumber);

        try { drawFloorEntities(); } catch (ConcurrentModificationException ignored) {}

        screen.setCharacter(
                Math.max(0, hero.getPosition().y - map[0].length + heroViewZone.y / 2) + Math.min(heroViewZone.y / 2, hero.getPosition().y) + 1,
                Math.max(0, hero.getPosition().x - map.length + heroViewZone.x / 2) + Math.min(heroViewZone.x / 2, hero.getPosition().x) + 2,
                hero.model);
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

    public static void drawData(TerminalPosition topLeft) {
        TextGraphics textGraphics = screen.newTextGraphics();
        TerminalSize size = new TerminalSize(
                stringsToDraw.stream().filter(Objects::nonNull).mapToInt((s) -> s.replaceAll("\u001b\\[[0-9;]*m", "").length()).max().orElse(0) + 2,
                stringsToDraw.size() + 1);
        textGraphics.fillRectangle(topLeft, size, ' ');

        drawRectangle(
                topLeft,
                size,
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
                "HP:" + StringColors.RED
                        + " ❤".repeat((int)(10 * (float)hero.getHitPoints() / (float)hero.getMaxHitPoints()))
                        + ((10 * hero.getHitPoints()) % hero.getMaxHitPoints() != 0 ? StringColors.RED_DANCING + " ❤" : "")
                        + StringColors.RESET
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
                "Number of enemies: " + floorEntities.size(),
                "Number of rooms: " + MapFactory.getNumberOfRooms()
        ));

        drawData(new TerminalPosition(heroViewZone.y + mapToMenuDistanceHorizontal + 2, 0));

        // Draw controls
        stringsToDraw = new ArrayList<>(List.of(
                "Controls",
                "D - attack | A - auto mode",
                "Esc - exit | R - regenerate map",
                "O - open chest | I - open inventory"
        ));

        // Draw inventory and chest
        if (isOpenedInventory) {
            stringsToDraw.addAll(List.of(
                    String.valueOf(Symbols.SINGLE_LINE_HORIZONTAL).repeat(stringsToDraw.stream().mapToInt((s) -> s.replaceAll("\u001b\\[[0-9;]*m", "").length()).max().orElse(0) + 2),
                    "C - clear selected container"
            ));
            if (openedChest != null) stringsToDraw.add("S - put/take item");
            Item item = null;
            if (inventoryWindowIsActive) {
                if (hero.inventory.isNotEmpty()) item = hero.inventory.getByIndex(inventoryCursorPosition);}
            else if (openedChest != null && openedChest.inventory.isNotEmpty()) item = openedChest.inventory.getByIndex(inventoryCursorPosition);
            if (item instanceof Equipment || item instanceof Weapon) stringsToDraw.add("E - equip");
            else if (item instanceof Potion) stringsToDraw.add("E - use");
        }

        drawData(new TerminalPosition(heroViewZone.y + mapToMenuDistanceHorizontal + 2, 11));

        // Draw log
        stringsToDraw = new ArrayList<>(List.of("Log"));
        stringsToDraw.addAll(log);

        drawData(new TerminalPosition(0, heroViewZone.x + 5));

        if (isOpenedInventory)
            try {
                drawInventoryMenu();
            } catch (ConcurrentModificationException ignored) {}
    }

    private static void drawInventoryMenu() throws ConcurrentModificationException {
        stringsToDraw.add("Inventory");
        if (hero.inventory.isNotEmpty()) {
            stringsToDraw.addAll(hero.inventory.getItems().stream().filter(Objects::nonNull).map(Item::getName).collect(Collectors.toList()));
            if (inventoryWindowIsActive)
                stringsToDraw.set(inventoryCursorPosition + 1, StringColors.GOLDEN + ">" + stringsToDraw.get(inventoryCursorPosition + 1) + StringColors.RESET);
        }
        int inventoryMenuWidth = stringsToDraw.stream().mapToInt((s) -> s.replaceAll("\u001b\\[[0-9;]*m", "").length()).max().orElse(0) + 2;
        drawData(new TerminalPosition(0, 0));
        if(openedChest != null) {
            stringsToDraw.add(openedChest.getName());
            if (openedChest.inventory.isNotEmpty()) {
                stringsToDraw.addAll(openedChest.inventory.getItems().stream().map(Item::getName).collect(Collectors.toList()));
                if (!inventoryWindowIsActive) stringsToDraw.set(inventoryCursorPosition + 1, StringColors.GOLDEN + ">" + stringsToDraw.get(inventoryCursorPosition + 1) + StringColors.RESET);
            }
            drawData(new TerminalPosition(inventoryMenuWidth, 0));
        }
    }

    public static void startGameLoop() {

        try {
            defaultTerminalFactory.setTerminalEmulatorTitle("DangeredOn " + GameResources.version);
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

            hero.receiveDamage(98);

            while (true) {
                TerminalSize newSize = screen.doResizeIfNecessary();
                if(newSize != null) {
                    terminalSize = newSize;
                    screen.clear();
                }

                if(gameOver) break;

                if(!hero.isAlive()) {
                    addToLog(StringColors.GREEN + hero.getName() + StringColors.RESET + " was slain");
                    floorNumber = 0;
                    refreshMap();
                    hero.instantRecovery();
                }

                if(lastKeys.contains("glmme")) {
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

            floorEntitiesControllers.forEach(Controller::cancel);
            gamePlayControllers.forEach(Controller::cancel);
            keyController.cancel();
        }
    }
}
