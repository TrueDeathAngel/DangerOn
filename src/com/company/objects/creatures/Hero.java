package com.company.objects.creatures;

import com.company.gameplay.InventoryMenu;
import com.company.gameplay.Controller;
import com.company.gameplay.GameLogic;
import com.company.objects.GameEntity;
import com.company.objects.items.Weapon;
import com.company.magic.Spell;
import com.company.map.CellTypes;
import com.company.map.MapFactory;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.company.Main.map;
import static com.company.gameplay.GameLogic.*;

public class Hero extends Mob
{
    private int currentLevel = 1;
    private int experiencePoints = 0;
    private int experiencePointsForNextLevel = 100;
    private int karma = 0;
    public int regenerationPower = 1;
    private final ArrayList<Spell<?>> spells = new ArrayList<>(3);
    public ArrayList<GameEntity> targets;

    public Hero(String name, int maxHitPoints, int attackPower, int defencePoints) {
        super(name, maxHitPoints, attackPower, defencePoints);
        model = new TextCharacter(
                '@',
                new TextColor.RGB(0, 200, 100),
                TextColor.ANSI.DEFAULT);
        setSlowness(3);
        setScanRadius(4);
        underCell = CellTypes.SAFE_AREA;
    }

    public int getCurrentLevel() { return currentLevel; }

    public void addExperiencePoints(int experiencePoints) { this.experiencePoints += experiencePoints; }

    public void newLevel() {
        experiencePoints -= experiencePointsForNextLevel;
        experiencePointsForNextLevel *= 1.5;
        experiencePointsForNextLevel -= experiencePointsForNextLevel % 10;
        currentLevel++;
    }

    public int getExperiencePoints() { return experiencePoints; }

    public int getExperiencePointsForNextLevel() { return experiencePointsForNextLevel; }

    public int getKarma() { return karma; }

    public boolean isFullHitPoints() { return maxHitPoints == hitPoints; }

    public void addSpell(Spell<?> spell) { if(spells.size() < 3) spells.add(spell); }

    public void removeSpell(Spell<?> spell) {spells.remove(spell);}

    public ArrayList<Spell<?>> getSpells(){ return spells; }

    public void showSpells() {
        System.out.println(getName() + "'s spells:");
        for(Spell<?> spell : spells) { System.out.println(spell.getSpell().getName()); }
    }

    public boolean canEquip(Weapon weapon)
    {
        return (currentLevel >= weapon.getRequiredLevel());
    }

    public ArrayList<GameEntity> scanAreaForTargets() {
        try {
            return GameLogic.floorEntities.stream().filter((gameEntity) -> gameEntity != null && gameEntity.scanArea(getScanRadius())).collect(Collectors.toCollection(ArrayList::new));
        }
        catch (ConcurrentModificationException e) { System.out.println(e.getMessage()); }
        return new ArrayList<>();
    }

    @Override
    public ArrayList<CellTypes> getAllowedCells() {
        return new ArrayList<>(List.of(CellTypes.EMPTY, CellTypes.DOOR, CellTypes.SAFE_AREA, CellTypes.EXIT));
    }

    @Override
    public void setPosition() {
        position = MapFactory.getHeroStartPosition();
        map[position.x][position.y] = getEntityType();
        underCell = CellTypes.SAFE_AREA;
    }

    public void openInventory() {
        isOpenedInventory = true;
        inventoryWindowIsActive = true;
        inventoryCursorPosition = 0;
        floorEntitiesControllers.forEach(Controller::pause);
        gamePlayControllers.forEach(Controller::pause);
    }

    public void openInventoryMenu() {
        openInventory();
        new InventoryMenu().start();
    }

    public void closeInventory() {
        isOpenedInventory = false;
        openedChest = null;
        gamePlayControllers.forEach(Controller::resume);
        floorEntitiesControllers.forEach(Controller::resume);
    }

    public void attackRandomTarget() {
        targets = hero.scanAreaForTargets();
        if(targets.size() > 0) {
            targets.stream()
                    .filter(Objects::nonNull)
                    .filter(GameEntity::isCloseToHero)
                    .findAny()
                    .ifPresent(floorEntity -> floorEntity.receiveDamage(hero.getDamage()));
        }
    }
}
