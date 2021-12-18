package com.company.objects;

import com.company.gameplay.GameLogic;
import com.company.map.CellTypes;
import com.company.map.MapFactory;
import com.company.objects.items.chests.Container;
import com.company.objects.items.Item;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.company.Main.map;
import static com.company.gameplay.GameLogic.*;

public abstract class GameEntity extends GameObject {
    private static int creaturesId;
    public final int id;
    public Point position;
    protected int hitPoints = 10;
    public CellTypes underCell;
    public TextCharacter model = new TextCharacter('?', TextColor.ANSI.YELLOW, TextColor.ANSI.DEFAULT);
    public final Container inventory;

    public GameEntity(String name, int inventorySize) {
        super(name);
        inventory = new Container(name + "'s inventory", inventorySize);
        id = creaturesId++;
    }

    public GameEntity(String name) {
        this(name, 10);
    }

    public Point getPosition() { return position; }

    public void setPosition() {
        setPosition(getFreePoint());
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public ArrayList<CellTypes> getAllowedCells()
    {
        return new ArrayList<>(List.of(CellTypes.EMPTY));
    }

    public CellTypes getEntityType() {
        return CellTypes.LAVA;
    }

    public Point getFreePoint() {
        Random random = new Random();
        while (true)
        {
            int x = random.nextInt(map.length), y = random.nextInt(map[x].length);
            if(getAllowedCells().contains(map[x][y])
                    && (getAllowedCells().contains(map[x + 1][y]) && getAllowedCells().contains(map[x - 1][y])
                        || (getAllowedCells().contains(map[x][y + 1]) && getAllowedCells().contains(map[x][y - 1])))
                    && x > MapFactory.getHeroStartPosition().x
                    && y > MapFactory.getHeroStartPosition().y) {
                underCell = map[x][y];
                map[x][y] = getEntityType();
                return new Point(x ,y);
            }
        }
    }

    public int getHitPoints() { return hitPoints; }

    public void receiveDamage(int damage) {
        hitPoints -= damage;
    }

    public boolean isAlive() { return hitPoints > 0; }

    public boolean scanArea(int scanRadius) {
        return Math.pow(position.x - GameLogic.hero.getPosition().x, 2) + Math.pow(position.y - GameLogic.hero.getPosition().y, 2) <= Math.pow(scanRadius, 2);
    }

    public boolean isCloseToHero() {
        return scanArea(1);
    }

    public void die() {
        floorEntities.remove(this);
        map[position.x][position.y] = underCell;
    }

    public ArrayList<Item> getLoot() {
        return inventory.getItems();
    }
}
