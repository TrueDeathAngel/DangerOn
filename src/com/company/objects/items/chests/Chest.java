package com.company.objects.items.chests;

import com.company.gameplay.InventoryMenu;
import com.company.map.CellTypes;
import com.company.objects.GameEntity;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;

import java.util.ArrayList;
import java.util.List;

import static com.company.gameplay.GameLogic.*;

public class Chest extends GameEntity {

    public Chest(String name, int inventorySize, TextCharacter model) {
        super(name, inventorySize);
        this.model = model;
    }

    public void open() {
        openedChest = this;
        hero.openInventory();
        new InventoryMenu(this).start();
    }

    @Override
    public void receiveDamage(int damage) {
        if(inventory.isNotEmpty()) addToLog("Cannot break a non-empty chest!");
        else super.receiveDamage(damage);
    }

    @Override
    public CellTypes getEntityType() {
        return CellTypes.CHEST;
    }

    @Override
    public ArrayList<CellTypes> getAllowedCells() {
        return new ArrayList<>(List.of(CellTypes.EMPTY, CellTypes.SAFE_AREA));
    }

    public enum ChestTypes {
        REGULAR,
        GRAVE,
        HERO
    }
}
