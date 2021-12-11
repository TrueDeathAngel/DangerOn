package com.company.objects.items;

import com.company.gameplay.Controller;
import com.company.gameplay.InventoryMenu;
import com.company.map.CellTypes;
import com.company.objects.GameEntity;

import java.awt.*;
import java.util.ArrayList;

import static com.company.gameplay.GameLogic.*;

public class Chest extends GameEntity {
    public final ArrayList<Item> items;

    public Chest(String name, Point position, ArrayList<Item> items, CellTypes underCell) {
        super(name);
        this.position = position;
        this.items = items;
        this.underCell = underCell;
        this.model = '†';
    }

    public void open() {
        openedChest = this;
        hero.openInventory();
        new InventoryMenu(this).start();
    }

    @Override
    public void receiveDamage(int damage) {
        if(items.isEmpty()) super.receiveDamage(damage);
        else addToLog("Cannot break a non-empty chest!");
    }

    @Override
    public CellTypes getEntityType() {
        return CellTypes.CHEST;
    }
}
