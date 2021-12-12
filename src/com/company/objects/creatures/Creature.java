package com.company.objects.creatures;

import com.company.gameplay.ChestController;
import com.company.objects.GameEntity;
import com.company.objects.items.Chest;
import com.company.map.CellTypes;
import com.company.recources.Colors;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import static com.company.Main.map;
import static com.company.gameplay.GameLogic.*;
import static com.company.gameplay.GameLogic.hero;

public class Creature extends GameEntity
{
    private final int attackPower;
    private final int defencePoints;
    private Status status = Status.IDLE;
    private int slowness;
    private int scanRadius = 2;
    protected final int maxHitPoints;

    public Creature(String name, int maxHitPoints, int attackPower, int defencePoints) {
        super(name);
        slowness = ThreadLocalRandom.current().nextInt(5) + 2;
        model = Character.toLowerCase(name.charAt(0));
        this.hitPoints = maxHitPoints;
        this.maxHitPoints = maxHitPoints;
        this.attackPower = attackPower;
        this.defencePoints = defencePoints;
    }

    public Status getStatus() { return status; }

    public void setStatus(Status status) { this.status = status; }

    public int getCost() {
        return maxHitPoints + attackPower + defencePoints;
    }

    public int getSlowness() { return slowness; }

    public void setSlowness(int slowness) { this.slowness = slowness; }

    public int getMaxHitPoints() { return maxHitPoints; }

    public int getAttackPower() { return attackPower; }

    public int getDamage() { return attackPower; }

    public int getDefencePoints() { return defencePoints; }

    public int getScanRadius() { return scanRadius; }

    public void setScanRadius(int scanRadius) { this.scanRadius = scanRadius; }

    @Override
    public void receiveDamage(int damage) {
        int defence = getDefencePoints();
        if(damage > defence) hitPoints -= damage - defence;
        else hitPoints--;
    }

    public void instantRecovery() { hitPoints = maxHitPoints; }

    public void move()
    {
        move(chooseNextPosition());
    }

    public void move(int x, int y) {
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

    public void moveToTarget(GameEntity gameEntity) {
        int dx = gameEntity.getPosition().x - position.x;
        int dy = gameEntity.getPosition().y - position.y;
        if(dy != 0 && canMove(0, dy / Math.abs(dy))) move(0, dy / Math.abs(dy));
        else if(dx != 0 && canMove(dx / Math.abs(dx), 0)) move(dx / Math.abs(dx), 0);
    }

    protected boolean canMove(int x, int y) {
        synchronized (map)
        {
            try {
                return getAllowedCells().contains(map[position.x + x][position.y + y]);
            }
            catch (Exception e) { System.out.println(position.x + x + ' ' + position.y + y); }
        }
        return false;
    }

    @Override
    public CellTypes getEntityType()
    {
        return CellTypes.CREATURE;
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

    public boolean scanArea() {
        return scanArea(scanRadius);
    }

    @Override
    public void die() {
        floorEntities.remove(this);
        map[position.x][position.y] = CellTypes.CHEST;
        Chest chest = new Chest(getName() + " items' chest", position, underCell);
        chest.inventory.addAllItems(getLoot());
        floorEntities.add(chest);
        ChestController chestController = new ChestController(chest);
        floorEntitiesControllers.add(chestController);
        chestController.start();
        addToLog(Colors.RED + getName() + Colors.RESET + " was slain. " + Colors.GOLDEN + "+ " + getCost() + " XP" + Colors.RESET);
        hero.addExperiencePoints(getCost());
    }
}
