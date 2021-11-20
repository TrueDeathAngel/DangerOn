package com.company.creatures;

import com.company.GameLogic;
import com.company.items.Weapon;
import com.company.magic.Spell;
import com.company.map.CellTypes;
import com.company.map.MapFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.company.Main.map;

public class Hero extends Mob
{
    private int currentLevel = 1;
    private int experiencePoints = 0;
    private int experiencePointsForNextLevel = 100;
    private int karma = 0;
    private ArrayList<Spell<?>> spells = new ArrayList<>(3);

    public Hero(String name, int maxHitPoints, int attackPower, int defencePoints)
    {
        super(name, maxHitPoints, attackPower, defencePoints);
        model = '@';
        setSlowness(3);
        setScanRadius(4);
        underCell = CellTypes.SAFE_AREA;
    }

    public int getCurrentLevel() { return currentLevel; }

    public void addExperiencePoints(int experiencePoints) { this.experiencePoints += experiencePoints; }

    public void newLevel() {
        experiencePoints -= experiencePointsForNextLevel;
        experiencePointsForNextLevel *= 2;
        currentLevel++;
    }

    public int getExperiencePoints() { return experiencePoints; }

    public int getExperiencePointsForNextLevel() { return experiencePointsForNextLevel; }

    public int getKarma() { return karma; }

    public void addSpell(Spell<?> spell) { if(spells.size() < 3) spells.add(spell); }

    public void removeSpell(Spell<?> spell) {spells.remove(spell);}

    public ArrayList<Spell<?>> getSpells(){ return spells; }

    public void showSpells()
    {
        System.out.println(getName() + "'s spells:");
        for(Spell<?> spell : spells) { System.out.println(spell.getSpell().getName()); }
    }

    public boolean canEquip(Weapon weapon)
    {
        return (currentLevel >= weapon.getRequiredLevel());
    }

    public ArrayList<Creature> scanAreaForTargets()
    {
        return GameLogic.creatures.stream().filter((creature) -> creature != null && creature.scanArea(getScanRadius())).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public ArrayList<CellTypes> getAllowedCells()
    {
        return new ArrayList<>(List.of(CellTypes.EMPTY, CellTypes.DOOR, CellTypes.SAFE_AREA));
    }

    @Override
    public void setStartPosition()
    {
        position = MapFactory.getHeroStartPosition();
        map[position.x][position.y] = getEntityType();
        underCell = CellTypes.SAFE_AREA;
    }
}
