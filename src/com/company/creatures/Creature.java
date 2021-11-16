package com.company.creatures;

import com.company.GameLogic;
import com.company.GameObject;
import com.company.items.Item;
import com.company.map.CellTypes;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static com.company.Main.map;

public class Creature extends GameObject
{
    private static int creaturesId;
    public final int id;
    private int hitPoints = 10;
    private int attackPower = 1;
    private int defencePoints = 0;
    private Status status = Status.IDLE;
    protected Point position;
    private int slowness;
    protected char model;
    private int scanRadius = 2;
    private ArrayList<Item> inventory;
    protected CellTypes underCell;

    public Creature(String name, int hitPoints, int attackPower, int defencePoints) {
        super(name);
        id = creaturesId++;
        slowness = ThreadLocalRandom.current().nextInt(3) + 2;
        model = Character.toLowerCase(name.charAt(0));
        this.hitPoints = hitPoints;
        this.attackPower = attackPower;
        this.defencePoints = defencePoints;
    }

    public Status getStatus() { return status; }

    public void setStatus(Status status) { this.status = status; }

    public int getSlowness() { return slowness; }

    public void setSlowness(int slowness) { this.slowness = slowness; }

    public int getAttackPower() { return attackPower; }

    public int getDamage() { return attackPower; }

    public int getHitPoints() { return hitPoints; }

    public int getDefencePoints() { return defencePoints; }

    public int getScanRadius() { return scanRadius; }

    public void setScanRadius(int scanRadius) { this.scanRadius = scanRadius; }

    public Point getPosition() { return position; }

    public void setPosition(Point position)
    {
            this.position = position;
    }

    public void setStartPosition()
    {
        position = getFreePoint();
    }

    public char getModel() { return model; }

    public void receiveDamage(int damage)
    {
        int defence = getDefencePoints();
        if(damage > defence) hitPoints -= damage - defence;
        else hitPoints--;
    }

    public boolean isAlive() { return getHitPoints() > 0; }

    public void move()
    {
        move(chooseNextPosition());
    }

    public void move(int x, int y)
    {
        if(canMove(x, y))
            try
            {
                map[position.x][position.y] = underCell;
                position.x += x;
                position.y += y;
                underCell = map[position.x][position.y];
                map[position.x][position.y] = getEntityType();
            }
            catch (Exception e) { e.printStackTrace(); }
    }

    public void move(Point point)
    {
        move(point.x, point.y);
    }

    public void moveToTarget(Creature creature) {
        int dx = creature.getPosition().x - position.x;
        int dy = creature.getPosition().y - position.y;
        if(dy != 0 && canMove(0, dy / Math.abs(dy))) move(0, dy / Math.abs(dy));
        else if(dx != 0 && canMove(dx / Math.abs(dx), 0)) move(dx / Math.abs(dx), 0);
    }

    protected boolean canMove(int x, int y) {
        synchronized (map)
        {
            return getAllowedCells().contains(map[position.x + x][position.y + y]);
        }
    }

    public CellTypes getEntityType()
    {
        return CellTypes.CREATURE;
    }

    public ArrayList<CellTypes> getAllowedCells()
    {
        return new ArrayList<>(List.of(CellTypes.EMPTY));
    }

    public boolean scanArea(int scanRadius) {
        return Math.abs(position.x - GameLogic.hero.getPosition().x) + Math.abs(position.y - GameLogic.hero.getPosition().y) <= scanRadius;
    }

    public boolean scanArea()
    {
        return scanArea(scanRadius);
    }

    public boolean isCloseToHero()
    {
        return scanArea(1);
    }

    public Point chooseNextPosition() {
        ArrayList<Point> directions = new ArrayList<>();
        for(byte i = -1; i <= 1; i++)
            for(byte j = -1; j <= 1; j++)
            {
                if(Math.abs(i) == Math.abs(j)) continue;
                try
                {
                    if(canMove(i, j))directions.add(new Point(i, j));
                }
                catch (IndexOutOfBoundsException ignored){}
                catch (Exception e) { System.out.println(e + " from Creature.chooseNextPosition()"); }
            }
        if(directions.size() > 0)
        {
            return directions.get(ThreadLocalRandom.current().nextInt(directions.size()));
        }
        return new Point(0, 0);
    }

    public Point getFreePoint()
    {
        Random random = new Random();
        int x, y;
        while (true)
        {
            x = random.nextInt(map.length);
            y = random.nextInt(map[x].length);
            if(getAllowedCells().contains(map[x][y]))
            {
                underCell = map[x][y];
                map[x][y] = getEntityType();
                return new Point(x ,y);
            }
        }
    }
}
