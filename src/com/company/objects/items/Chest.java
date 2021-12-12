package com.company.objects.items;

import com.company.gameplay.Controller;
import com.company.gameplay.InventoryMenu;
import com.company.map.CellTypes;
import com.company.objects.GameEntity;

import java.awt.*;
import static com.company.gameplay.GameLogic.*;

public class Chest extends GameEntity {

    public Chest(String name, Point position, CellTypes underCell) {
        super(name);
        this.position = position;
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
        if(!inventory.isNotEmpty()) super.receiveDamage(damage);
        else addToLog("Cannot break a non-empty chest!");
    }

    @Override
    public CellTypes getEntityType() {
        return CellTypes.CHEST;
    }
}
